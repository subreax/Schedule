package com.subreax.schedule.ui.scheduleownermgr.ownerpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.ui.SearchScheduleIdUseCase
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleOwnerPickerViewModel @Inject constructor(
    private val ownerRepository: ScheduleOwnerRepository
) : ViewModel() {
    private val searchScheduleIdUseCase = SearchScheduleIdUseCase(
        ownerRepository,
        { errors.send(it) },
        viewModelScope
    )

    val searchId = searchScheduleIdUseCase.searchId
    val hints = searchScheduleIdUseCase.hints
    val isHintsLoading = searchScheduleIdUseCase.isLoading

    private val _navBackEvent = MutableSharedFlow<Unit>()

    val navBackEvent: Flow<Unit> = _navBackEvent

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    val errors = Channel<UiText>()

    init {
        viewModelScope.launch {
            searchScheduleIdUseCase.existingIds = getExistingIds()
        }
    }

    fun updateSearchId(searchId: String) {
        searchScheduleIdUseCase.search(searchId)
    }

    fun saveId(scheduleId: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val res = ownerRepository.addOwner(scheduleId)
            if (res is Resource.Failure) {
                errors.send(res.message)
            } else {
                _navBackEvent.emit(Unit)
            }
            _isSubmitting.value = false
        }
    }

    private suspend fun getExistingIds(): List<String> {
        return ownerRepository.getOwners().value
            .map { it.networkId }
    }
}
