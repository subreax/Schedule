package com.subreax.schedule.ui.bookmark_manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkManagerViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    val bookmarks: Flow<List<ScheduleBookmark>> = bookmarkRepository.bookmarks

    var isDialogShown by mutableStateOf(false)
        private set

    private var dialogBookmark: ScheduleBookmark? = null
    var dialogName by mutableStateOf("")
        private set

    fun deleteBookmark(bookmark: ScheduleBookmark) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmark.scheduleId.value)
        }
    }

    fun showEditNameDialog(bookmark: ScheduleBookmark) {
        dialogBookmark = bookmark
        dialogName = bookmark.name
        isDialogShown = true
    }

    fun bookmarkNameChanged(name: String) {
        dialogName = name
    }

    fun updateBookmarkName() {
        viewModelScope.launch {
            bookmarkRepository.setBookmarkName(dialogBookmark!!.scheduleId.value, dialogName)
            dismissDialog()
        }
    }

    fun dismissDialog() {
        dialogBookmark = null
        isDialogShown = false
    }
}
