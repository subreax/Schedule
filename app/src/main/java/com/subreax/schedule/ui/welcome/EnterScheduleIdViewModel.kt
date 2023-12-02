package com.subreax.schedule.ui.welcome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterScheduleIdViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {
    private var getHintsJob: Job = Job()

    var scheduleId by mutableStateOf("")
        private set

    var hints = mutableStateListOf<String>()
        private set

    var errorText: UiText? by mutableStateOf(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _navHomeEvent = MutableSharedFlow<Unit>()
    val navHomeEvent: SharedFlow<Unit>
        get() = _navHomeEvent

    fun updateScheduleId(newValue: String) {
        scheduleId = newValue
        errorText = null

        getHintsJob.cancel()
        if (newValue.isNotBlank()) {
            getHintsJob = viewModelScope.launch {
                val newHints = repository.getScheduleIdHints(newValue)
                hints.clear()
                hints.addAll(newHints)
            }
        }
        else {
            hints.clear()
        }
    }

    fun submit() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.addScheduleId(scheduleId)
            isLoading = false

            if (result is Resource.Failure) {
                errorText = result.message
            } else {
                _navHomeEvent.emit(Unit)
            }
        }
    }
}