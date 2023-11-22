package com.subreax.schedule.ui.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.DateFormatter
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {
    sealed class ScheduleItem {
        data class Subject(
            val id: Int,
            val name: String,
            val place: String,
            val timeRange: String,
            val teacherName: String,
            val type: SubjectType
        ) : ScheduleItem()

        data class Title(val title: String) : ScheduleItem()
    }

    var schedule = mutableStateListOf<ScheduleItem>()
        private set

    var scheduleOwners = mutableStateListOf<ScheduleOwner>()
        private set

    var currentScheduleOwner by mutableStateOf(ScheduleOwner(""))
        private set

    private val _errors = MutableSharedFlow<UiText>()
    val errors: SharedFlow<UiText>
        get() = _errors.asSharedFlow()

    init {
        viewModelScope.launch {
            scheduleOwners.addAll(scheduleRepository.getScheduleOwners())
            val lastRequestedScheduleId = scheduleRepository.getLastRequestedScheduleId()
            currentScheduleOwner = scheduleOwners.find {
                it.id == lastRequestedScheduleId
            }!!

            schedule.addAll(getSchedule(lastRequestedScheduleId))
        }
    }

    fun loadSchedule(scheduleOwner: ScheduleOwner) {
        if (currentScheduleOwner.id != scheduleOwner.id) {
            viewModelScope.launch {
                currentScheduleOwner = scheduleOwner

                schedule.clear()
                schedule.addAll(getSchedule(scheduleOwner.id))
            }
        }
    }

    private suspend fun getSchedule(group: String): List<ScheduleItem> = withContext(Dispatchers.IO) {
        val result = scheduleRepository.getScheduleForGroup(group)
        val schedule1 = mutableListOf<ScheduleItem>()

        val calendar = Calendar.getInstance()
        var oldSubjectDay = -1

        var errorMessage: UiText? = null
        val repoSchedule = when (result) {
            is Resource.Success -> {
                result.value
            }

            is Resource.Failure -> {
                errorMessage = result.message
                result.cachedValue ?: emptyList()
            }
        }

        repoSchedule.forEach {
            val subjectDay = getDayOfMonth(calendar, it.timeRange.start)

            if (oldSubjectDay != subjectDay) {
                val title = DateFormatter.format(appContext, it.timeRange.start)
                schedule1.add(ScheduleItem.Title(title))
                oldSubjectDay = subjectDay
            }

            schedule1.add(
                ScheduleItem.Subject(
                    id = it.id,
                    name = it.name,
                    place = it.place,
                    timeRange = it.timeRange.toString(calendar),
                    teacherName = it.teacherName?.compact() ?: "",
                    type = it.type
                )
            )
        }

        if (errorMessage != null) {
            viewModelScope.launch {
                _errors.emit(errorMessage)
            }
        }

        schedule1
    }

    private fun getDayOfMonth(calendar: Calendar, time: Date): Int {
        calendar.time = time
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}