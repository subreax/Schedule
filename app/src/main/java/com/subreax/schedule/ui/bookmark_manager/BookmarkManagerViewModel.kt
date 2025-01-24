package com.subreax.schedule.ui.bookmark_manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections

class BookmarkManagerViewModel(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val _bookmarks = MutableStateFlow(listOf<ScheduleBookmark>())
    val bookmarks: Flow<List<ScheduleBookmark>> = _bookmarks.asStateFlow()

    private val _bookmarkToRename = MutableStateFlow<ScheduleBookmark?>(null)
    val bookmarkToRename = _bookmarkToRename.asStateFlow()

    private val _newBookmarkName = MutableStateFlow("")
    val newBookmarkName = _newBookmarkName.asStateFlow()

    private val _deletedBookmark = MutableSharedFlow<ScheduleBookmark>()
    val deletedBookmark = _deletedBookmark.asSharedFlow()

    private var bookmarkCollectorJob: Job = Job()

    init {
        startBookmarkCollector()
    }

    fun deleteBookmark(bookmark: ScheduleBookmark) {
        viewModelScope.launch {
            bookmarkRepository.getBookmark(bookmark.scheduleId.value).ifSuccess {
                bookmarkRepository.deleteBookmark(it.scheduleId.value)
                _deletedBookmark.emit(it)
                Resource.Success(Unit)
            }
        }
    }

    fun showBookmarkRenameDialog(bookmark: ScheduleBookmark) {
        _bookmarkToRename.value = bookmark
        _newBookmarkName.value = bookmark.name
    }

    fun dialogBookmarkNameChanged(name: String) {
        _newBookmarkName.value = name
    }

    fun moveBookmark(from: Int, to: Int) {
        val list = _bookmarks.value.toMutableList()
        Collections.swap(list, from, to)
        _bookmarks.value = list

        viewModelScope.launch {
            bookmarkRepository.swapBookmarkPositions(from, to)
        }
    }

    fun addBookmark(bookmark: ScheduleBookmark) {
        viewModelScope.launch {
            with(bookmark) {
                bookmarkRepository.addBookmark(scheduleId.value, name, position.coerceAtLeast(0))
            }
        }
    }

    fun updateBookmarkName() {
        viewModelScope.launch {
            val scheduleId = _bookmarkToRename.value!!.scheduleId.value
            val newName = _newBookmarkName.value
            bookmarkRepository.setBookmarkName(scheduleId, newName)
            dismissRenameBookmarkDialog()
        }
    }

    fun dismissRenameBookmarkDialog() {
        _bookmarkToRename.value = null
    }

    fun onDragChanged(isDragging: Boolean) {
        if (isDragging) {
            stopBookmarkCollector()
        } else {
            startBookmarkCollector()
        }
    }

    private fun startBookmarkCollector() {
        bookmarkCollectorJob.cancel()
        bookmarkCollectorJob = viewModelScope.launch {
            bookmarkRepository.bookmarks.collect {
                _bookmarks.value = it
            }
        }
    }

    private fun stopBookmarkCollector() {
        bookmarkCollectorJob.cancel()
    }
}
