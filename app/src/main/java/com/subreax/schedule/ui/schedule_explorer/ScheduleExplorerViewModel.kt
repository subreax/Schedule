package com.subreax.schedule.ui.schedule_explorer

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.R
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.usecase.ScheduleUseCases
import com.subreax.schedule.data.usecase.SubjectUseCases
import com.subreax.schedule.ui.SubjectDetailsContainer
import com.subreax.schedule.ui.ScheduleContainer
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.ifFailure
import com.subreax.schedule.utils.urlDecode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ScheduleExplorerViewModel(
    appContext: Context,
    savedStateHandle: SavedStateHandle,
    scheduleUseCases: ScheduleUseCases,
    subjectUseCases: SubjectUseCases,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    val scheduleId = savedStateHandle.get<String>("id")!!.urlDecode()

    private val scheduleContainer = ScheduleContainer(scheduleUseCases, appContext, viewModelScope)
    private val subjectDetailsContainer =
        SubjectDetailsContainer(subjectUseCases, bookmarkRepository)

    val uiSchedule = scheduleContainer.schedule
    val uiLoadingState = scheduleContainer.loadingState
    val pickedSubject = subjectDetailsContainer.subject

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked = _isBookmarked.asStateFlow()

    private val _isCreateBookmarkDialogShown = MutableStateFlow(false)
    val isCreateBookmarkDialogShown = _isCreateBookmarkDialogShown.asStateFlow()

    private val _bookmarkName = MutableStateFlow("")
    val bookmarkName = _bookmarkName.asStateFlow()

    val messages = Channel<UiText>()

    init {
        viewModelScope.launch {
            scheduleContainer.update(scheduleId)
            launch {
                val bookmark = bookmarkRepository.getBookmark(scheduleId).ifFailure { null }
                _isBookmarked.value = bookmark != null
            }

            uiLoadingState
                .filter { it is UiLoadingState.Error }
                .collect {
                    messages.send((it as UiLoadingState.Error).message)
                }
        }
    }

    fun refreshIfNeeded() {
        scheduleContainer.refreshIfNeeded()
    }

    fun cancelSync() {
        scheduleContainer.cancelSync()
    }

    fun openSubjectDetails(subjectId: Long) {
        val scheduleType = uiSchedule.value.id.type
        viewModelScope.launch {
            subjectDetailsContainer.show(subjectId, scheduleType)
        }
    }

    fun hideSubjectDetails() {
        subjectDetailsContainer.hide()
    }

    fun showCreateBookmarkDialog() {
        _bookmarkName.value = ""
        _isCreateBookmarkDialogShown.value = true
    }

    fun hideCreateBookmarkDialog() {
        _isCreateBookmarkDialogShown.value = false
    }

    fun addBookmark() {
        viewModelScope.launch {
            val name = _bookmarkName.value.ifEmpty { null }
            val res = bookmarkRepository.addBookmark(scheduleId, name)
            when (res) {
                is Resource.Success -> {
                    messages.send(UiText.res(R.string.bookmark_added))
                    _isBookmarked.value = true
                }

                is Resource.Failure -> messages.send(res.message)
            }
        }
    }

    fun removeBookmark() {
        viewModelScope.launch {
            _bookmarkName.value = bookmarkRepository.getBookmark(scheduleId)
                .ifFailure { null }
                ?.name ?: ""

            val res = bookmarkRepository.deleteBookmark(scheduleId)
            if (res is Resource.Success) {
                _isBookmarked.value = false
            }
        }
    }

    fun updateBookmarkName(name: String) {
        _bookmarkName.value = name
    }
}
