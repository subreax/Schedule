package com.subreax.schedule.ui.bookmark_manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkManagerViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    val bookmarks: StateFlow<List<ScheduleBookmark>> = bookmarkRepository.bookmarks
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _bookmarkToRename = MutableStateFlow<ScheduleBookmark?>(null)
    val bookmarkToRename = _bookmarkToRename.asStateFlow()

    private val _newBookmarkName = MutableStateFlow("")
    val newBookmarkName = _newBookmarkName.asStateFlow()

    fun deleteBookmark(bookmark: ScheduleBookmark) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmark.scheduleId.value)
        }
    }

    fun showBookmarkRenameDialog(bookmark: ScheduleBookmark) {
        _bookmarkToRename.value = bookmark
        _newBookmarkName.value = bookmark.name
    }

    fun dialogBookmarkNameChanged(name: String) {
        _newBookmarkName.value = name
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
}
