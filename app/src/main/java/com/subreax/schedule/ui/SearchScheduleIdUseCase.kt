package com.subreax.schedule.ui

import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchScheduleIdUseCase(
    private val ownerRepository: ScheduleOwnerRepository,
    private val scope: CoroutineScope
) {
    var existingIds = emptyList<String>()

    private val _searchId = MutableStateFlow("")
    val searchId: StateFlow<String>
        get() = _searchId

    @OptIn(FlowPreview::class)
    val suggestions: StateFlow<List<String>> = _searchId
        .debounce(300)
        .map { id ->
            if (id.isBlank()) {
                emptyList()
            } else {
                ownerRepository.getHints(id)
                    .filter { !existingIds.contains(it) }
            }
        }
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(2000L),
            emptyList()
        )

    fun search(id: String) {
        _searchId.value = id
    }
}