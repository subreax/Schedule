package com.subreax.schedule.ui.bookmark_manager.add_bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.ui.SearchScheduleIdUseCase
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddBookmarkViewModel(
    scheduleIdRepository: ScheduleIdRepository,
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

    fun updateSearchId(searchId: String) {
        searchScheduleIdUseCase.search(searchId)
    }

    fun addBookmark(scheduleId: String) {
        viewModelScope.launch {
            withLoading {
                when (val res = bookmarkRepository.addBookmark(scheduleId)) {
                    is Resource.Success -> _navBackEvent.emit(Unit)
                    is Resource.Failure -> errors.send(res.message)
                }
            }
        }
    }

    private inline fun <T> withLoading(block: () -> T): T {
        _isSubmitting.value = true
        val result = block()
        _isSubmitting.value = false
        return result
    }
}
