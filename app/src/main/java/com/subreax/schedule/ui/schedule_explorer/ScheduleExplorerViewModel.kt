package com.subreax.schedule.ui.schedule_explorer

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.GetScheduleUseCase
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.UiSubjectDetails
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.ifFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class ScheduleExplorerViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    savedStateHandle: SavedStateHandle,
    scheduleRepository: ScheduleRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    val scheduleId = savedStateHandle.get<String>("id")!!.urlDecode()

    private val getScheduleUseCase =
        GetScheduleUseCase(scheduleRepository, bookmarkRepository, appContext, viewModelScope)

    val uiSchedule = getScheduleUseCase.schedule
    val uiLoadingState = getScheduleUseCase.loadingState

    private val _pickedSubject = MutableStateFlow<UiSubjectDetails?>(null)
    val pickedSubject = _pickedSubject.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked = _isBookmarked.asStateFlow()

    private val _isCreateBookmarkDialogShown = MutableStateFlow(false)
    val isCreateBookmarkDialogShown = _isCreateBookmarkDialogShown.asStateFlow()

    private val _bookmarkName = MutableStateFlow("")
    val bookmarkName = _bookmarkName.asStateFlow()

    val messages = Channel<UiText>()

    init {
        viewModelScope.launch {
            getScheduleUseCase.init(scheduleId)
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

    fun onStart() {
        getScheduleUseCase.refreshIfExpired()
    }

    fun openSubjectDetails(subjectId: Long) {
        viewModelScope.launch {
            when (val res = getScheduleUseCase.getSubjectDetails(subjectId)) {
                is Resource.Success -> _pickedSubject.value = res.value
                is Resource.Failure -> messages.send(res.message)
            }
        }
    }

    fun hideSubjectDetails() {
        _pickedSubject.value = null
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
                    messages.send(UiText.hardcoded("Закладка добавлена"))
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

    fun refresh() {
        getScheduleUseCase.refresh()
    }

    private fun String.urlDecode(): String {
        return URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
    }
}
