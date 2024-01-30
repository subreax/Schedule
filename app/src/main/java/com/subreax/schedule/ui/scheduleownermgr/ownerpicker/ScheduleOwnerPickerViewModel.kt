package com.subreax.schedule.ui.scheduleownermgr.ownerpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.ui.SearchScheduleIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleOwnerPickerViewModel @Inject constructor(
    private val ownerRepository: ScheduleOwnerRepository
) : ViewModel() {
    private val searchScheduleIdUseCase = SearchScheduleIdUseCase(ownerRepository, viewModelScope)

    val searchId: StateFlow<String> = searchScheduleIdUseCase.searchId
    val suggestions: StateFlow<List<String>> = searchScheduleIdUseCase.suggestions

    private val _navBackEvent = MutableSharedFlow<Unit>()
    val navBackEvent: Flow<Unit>
        get() = _navBackEvent

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
            ownerRepository.addOwner(scheduleId) // should be ok
            _navBackEvent.emit(Unit)
        }
    }

    private suspend fun getExistingIds(): List<String> {
        return ownerRepository.getOwners().value
            .map { it.networkId }
    }
}
