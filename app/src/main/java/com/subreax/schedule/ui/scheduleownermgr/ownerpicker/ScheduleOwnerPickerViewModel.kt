package com.subreax.schedule.ui.scheduleownermgr.ownerpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleOwnerPickerViewModel @Inject constructor(
    private val ownerRepository: ScheduleOwnerRepository
) : ViewModel() {
    private var existingIds = listOf<String>()

    private val _searchId = MutableStateFlow("")
    val searchId: StateFlow<String>
        get() = _searchId

    @OptIn(FlowPreview::class)
    val suggestions: StateFlow<List<String>> = _searchId
        .debounce(500)
        .map { id ->
            if (id.isBlank()) {
                emptyList()
            } else {
                ownerRepository.getHints(id)
                    .filter { !existingIds.contains(it) }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000L),
            emptyList()
        )

    private val _navBackEvent = MutableSharedFlow<Unit>()
    val navBackEvent: Flow<Unit>
        get() = _navBackEvent

    init {
        viewModelScope.launch {
            existingIds = ownerRepository.getOwners().value.map { it.networkId }
        }
    }

    fun updateSearchId(searchId: String) {
        _searchId.value = searchId
    }

    fun saveId(scheduleId: String) {
        viewModelScope.launch {
            ownerRepository.addOwner(scheduleId) // should be ok
            _navBackEvent.emit(Unit)
        }
    }
}
