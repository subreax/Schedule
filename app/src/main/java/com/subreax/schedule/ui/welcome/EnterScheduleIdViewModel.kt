package com.subreax.schedule.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.ui.SearchScheduleIdUseCase
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterScheduleIdViewModel @Inject constructor(
    private val repository: ScheduleOwnerRepository
) : ViewModel() {
    private val searchScheduleIdUseCase = SearchScheduleIdUseCase(
        repository,
        { _error.value = it },
        viewModelScope
    )

    val searchId = searchScheduleIdUseCase.searchId
    val suggestions = searchScheduleIdUseCase.hints

    private val _error = MutableStateFlow<UiText?>(null)
    val error: StateFlow<UiText?> = _error

    val areHintsLoading = searchScheduleIdUseCase.isLoading

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _navHomeEvent = MutableSharedFlow<Unit>()
    val navHomeEvent: SharedFlow<Unit>
        get() = _navHomeEvent

    fun updateSearchId(newValue: String) {
        _error.value = null
        searchScheduleIdUseCase.search(newValue)
    }

    fun submit(id: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.addOwner(id)
            _isSubmitting.value = false

            if (result is Resource.Failure) {
                _error.value = result.message
            } else {
                _navHomeEvent.emit(Unit)
            }
        }
    }
}