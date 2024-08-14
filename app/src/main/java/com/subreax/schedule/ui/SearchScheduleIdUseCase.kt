package com.subreax.schedule.ui

import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchScheduleIdUseCase(
    private val scheduleIdRepository: ScheduleIdRepository,
    private val onError: suspend (UiText) -> Unit,
    scope: CoroutineScope
) {
    private val _searchId = MutableStateFlow("")
    val searchId: StateFlow<String> = _searchId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    @OptIn(FlowPreview::class)
    val hints = _searchId
        .debounce(500)
        .map { id ->
            val res = if (id.isBlank()) {
                emptyList()
            } else {
                when (val res = scheduleIdRepository.getScheduleIds(id.trim())) {
                    is Resource.Success -> res.value

                    is Resource.Failure -> {
                        onError(res.message)
                        emptyList()
                    }
                }
            }
            res.map { it.value }.also {
                _isLoading.value = false
            }
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(2000L), emptyList())

    fun search(id: String) {
        _isLoading.value = true
        _searchId.value = id
    }
}