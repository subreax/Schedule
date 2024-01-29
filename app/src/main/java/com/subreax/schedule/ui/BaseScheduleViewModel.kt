package com.subreax.schedule.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.repository.schedule.provider.ScheduleProvider
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleItem
import com.subreax.schedule.ui.component.scheduleitemlist.toScheduleItems
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


abstract class BaseScheduleViewModel(
    private val appContext: Context,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    protected var scheduleProvider: ScheduleProvider? = null

    var scheduleItems = mutableStateListOf<ScheduleItem>()
        private set

    var currentScheduleOwner by mutableStateOf(ScheduleOwner("", ScheduleOwner.Type.Student, ""))
        protected set

    var pickedSubject by mutableStateOf<Subject?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    protected val _errors = MutableSharedFlow<UiText>()
    val errors: SharedFlow<UiText>
        get() = _errors

    private val _navToDetails = MutableSharedFlow<Unit>()
    val navToDetails: SharedFlow<Unit>
        get() = _navToDetails

    fun getSchedule(ownerNetworkId: String) {
        if (currentScheduleOwner.networkId != ownerNetworkId) {
            viewModelScope.launch {
                scheduleItems.clear()
                isLoading = true
                _getSchedule(ownerNetworkId)
                isLoading = false
            }
        }
    }

    private suspend fun _getSchedule(ownerNetworkId: String) {
        val providerResult = scheduleRepository.getScheduleProvider(ownerNetworkId)
        if (providerResult is Resource.Failure) {
            _errors.emit(providerResult.message)
            return
        }

        val provider = (providerResult as Resource.Success).value
        scheduleProvider = provider
        currentScheduleOwner = provider.getOwner()

        val subjectsResult = provider.getSubjects()
        if (subjectsResult is Resource.Success) {
            val subjects = subjectsResult.value
            val items = subjects.toScheduleItems(appContext, currentScheduleOwner.type)
            scheduleItems.addAll(items)
        } else if (subjectsResult is Resource.Failure) {
            subjectsResult.cachedValue?.let {
                val items = it.toScheduleItems(appContext, currentScheduleOwner.type)
                scheduleItems.addAll(items)
            }

            _errors.emit(subjectsResult.message)
        }
    }

    fun openSubjectDetails(subjectId: Long) {
        viewModelScope.launch {
            val subject = scheduleProvider!!.getSubjectById(subjectId)
            if (subject != null) {
                pickedSubject = subject
                _navToDetails.emit(Unit)
            } else {
                _errors.emit(UiText.hardcoded("Предмет не найден :/"))
            }
        }
    }
}