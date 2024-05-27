package com.subreax.schedule.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule.provider.ScheduleProvider
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleItem
import com.subreax.schedule.ui.component.scheduleitemlist.toScheduleItems
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.approxBinarySearch
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.coroutineContext

data class UiSchedule(
    val owner: ScheduleOwner = "".toStudentScheduleOwner(),
    val items: List<ScheduleItem> = emptyList(),
    val todayItemIndex: Int = 0,
    val loadingState: LoadingState = LoadingState.Done
) {
    companion object {
        fun loading(owner: ScheduleOwner = "".toStudentScheduleOwner()): UiSchedule {
            return UiSchedule(owner = owner, loadingState = LoadingState.InProgress)
        }

        fun error(
            owner: ScheduleOwner = "".toStudentScheduleOwner(),
            items: List<ScheduleItem> = emptyList(),
            todayItemIndex: Int = 0
        ): UiSchedule {
            return UiSchedule(
                owner = owner,
                items = items,
                todayItemIndex = todayItemIndex,
                loadingState = LoadingState.Failed
            )
        }

        fun success(
            owner: ScheduleOwner,
            items: List<ScheduleItem>,
            todayItemIndex: Int
        ): UiSchedule {
            return UiSchedule(
                owner = owner,
                items = items,
                todayItemIndex = todayItemIndex,
                loadingState = LoadingState.Done
            )
        }
    }
}

private fun String.toStudentScheduleOwner(): ScheduleOwner {
    return ScheduleOwner(this, ScheduleOwner.Type.Student, "")
}


abstract class BaseScheduleViewModel(
    protected val appContext: Context,
    protected val scheduleRepository: ScheduleRepository
) : ViewModel() {
    data class SubjectDetails(
        val subjectId: Long,
        val ownerType: ScheduleOwner.Type,
        val name: String,
        val nameAlias: String,
        val type: SubjectType,
        val teacher: String,
        val date: String,
        val time: String,
        val place: String,
        val groups: List<Group>,
        val note: String
    )

    private var scheduleProvider: ScheduleProvider? = null
    private var getScheduleJob: Job = Job()

    private val _uiSchedule = MutableStateFlow(UiSchedule())
    val uiSchedule = _uiSchedule.asStateFlow()

    var pickedSubject by mutableStateOf<SubjectDetails?>(null)
        private set

    val errors = Channel<UiText>()


    fun getSchedule(ownerNetworkId: String) {
        val currentScheduleOwner = _uiSchedule.value.owner
        if (currentScheduleOwner.networkId != ownerNetworkId) {
            getScheduleJob.cancel()
            getScheduleJob = viewModelScope.launch {
                _getSchedule(ownerNetworkId)
            }
        }
    }

    fun updateSchedule() {
        val currentOwnerNetworkId = _uiSchedule.value.owner.networkId
        if (currentOwnerNetworkId.isNotEmpty()) {
            getScheduleJob.cancel()
            getScheduleJob = viewModelScope.launch {
                _getSchedule(currentOwnerNetworkId)
                pickedSubject?.let {
                    openSubjectDetails(it.subjectId)
                }
            }
        }
    }

    private suspend fun _getSchedule(ownerNetworkId: String) {
        _uiSchedule.value = UiSchedule.loading(ownerNetworkId.toStudentScheduleOwner())

        val providerRes = scheduleRepository.getScheduleProvider(ownerNetworkId)
        if (providerRes is Resource.Failure) {
            notifyError(providerRes.message)
            _uiSchedule.value = UiSchedule.error(ownerNetworkId.toStudentScheduleOwner())
            return
        }

        val provider = (providerRes as Resource.Success).value
        scheduleProvider = provider
        val owner = provider.getOwner()
        _uiSchedule.value = UiSchedule.loading(owner)

        when (val subjectsRes = provider.getSubjects()) {
            is Resource.Success -> {
                val items = subjectsRes.value.toScheduleItems(appContext, owner.type)
                val todayItemIndex = getTodayItemIndex(items)
                coroutineContext.ensureActive()
                _uiSchedule.value = UiSchedule.success(owner, items, todayItemIndex)
            }

            is Resource.Failure -> {
                val items = subjectsRes.cachedValue?.toScheduleItems(appContext, owner.type)
                    ?: emptyList()

                val todayItemIndex = getTodayItemIndex(items)
                coroutineContext.ensureActive()
                _uiSchedule.value = UiSchedule.error(owner, items, todayItemIndex)
                notifyError(subjectsRes.message)
            }
        }
    }

    private fun getTodayItemIndex(items: List<ScheduleItem>): Int {
        if (items.isEmpty()) {
            return 0
        }

        val calendar = android.icu.util.Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val today = calendar.time
        val (left, right) = items.approxBinarySearch { it.date.compareTo(today) }
        return if (items[left] is ScheduleItem.Title) {
            left
        } else {
            right
        }
    }

    fun openSubjectDetails(subjectId: Long) {
        viewModelScope.launch {
            val subject = scheduleProvider!!.getSubjectById(subjectId)
            if (subject != null) {
                val ownerType = scheduleProvider!!.getOwner().type
                val groups = if (ownerType != ScheduleOwner.Type.Student) {
                    subject.groups
                } else {
                    emptyList()
                }

                val note = if (ownerType == ScheduleOwner.Type.Student) {
                    subject.groups.first().note
                } else {
                    ""
                }

                pickedSubject = SubjectDetails(
                    subjectId = subject.id,
                    ownerType = ownerType,
                    name = subject.name,
                    nameAlias = subject.nameAlias,
                    type = subject.type,
                    teacher = subject.teacher?.full() ?: "",
                    date = formatDate(subject.timeRange.start),
                    time = subject.timeRange.toString(Calendar.getInstance()),
                    place = subject.place,
                    groups = groups,
                    note = note
                )
            } else {
                // hide details
                pickedSubject = null
            }
        }
    }

    fun hideSubjectDetails() {
        pickedSubject = null
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date)
    }

    private suspend fun notifyError(msg: UiText) {
        errors.send(msg)
    }
}
