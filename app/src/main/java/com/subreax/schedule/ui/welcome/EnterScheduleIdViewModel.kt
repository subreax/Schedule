package com.subreax.schedule.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
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
    scheduleIdRepository: ScheduleIdRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val searchScheduleIdUseCase = SearchScheduleIdUseCase(
        scheduleIdRepository,
        bookmarkRepository,
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
            withLoading {
                when (val res = bookmarkRepository.addBookmark(id)) {
                    is Resource.Success -> _navHomeEvent.emit(Unit)
                    is Resource.Failure -> _error.value = res.message
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