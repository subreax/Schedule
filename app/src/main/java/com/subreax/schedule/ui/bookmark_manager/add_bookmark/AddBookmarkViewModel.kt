package com.subreax.schedule.ui.bookmark_manager.add_bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
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
class AddBookmarkViewModel @Inject constructor(
    private val scheduleIdRepository: ScheduleIdRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val searchScheduleIdUseCase = SearchScheduleIdUseCase(
        scheduleIdRepository,
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
            when (val existingIdsRes = scheduleIdRepository.getScheduleIds()) {
                is Resource.Success -> {
                    searchScheduleIdUseCase.existingIds = existingIdsRes.value.map { it.value }
                }

                is Resource.Failure -> {
                    errors.send(existingIdsRes.message)
                }
            }
        }
    }

    fun updateSearchId(searchId: String) {
        searchScheduleIdUseCase.search(searchId)
    }

    fun saveId(scheduleId: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val res = scheduleIdRepository.getScheduleId(scheduleId)
                .ifSuccess { bookmarkRepository.addBookmark(it) }

            if (res is Resource.Failure) {
                errors.send(res.message)
            } else {
                _navBackEvent.emit(Unit)
            }
            _isSubmitting.value = false
        }
    }
}
