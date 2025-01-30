package com.subreax.schedule.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.usecase.ScheduleUseCases
import com.subreax.schedule.data.usecase.SubjectUseCases
import com.subreax.schedule.ui.SubjectDetailsContainer
import com.subreax.schedule.ui.ScheduleContainer
import com.subreax.schedule.ui.SyncType
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.ifFailure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    appContext: Context,
    scheduleUseCases: ScheduleUseCases,
    private val subjectUseCases: SubjectUseCases,
    bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val scheduleContainer = ScheduleContainer(scheduleUseCases, appContext, viewModelScope)
    private val subjectDetailsContainer =
        SubjectDetailsContainer(subjectUseCases, bookmarkRepository)

    private val _selectedBookmark = MutableStateFlow(
        ScheduleBookmark(ScheduleId("", ScheduleType.Unknown))
    )

    private val selectedScheduleId: ScheduleId
        get() = _selectedBookmark.value.scheduleId

    val schedule = scheduleContainer.schedule
    val loadingState = scheduleContainer.loadingState
    val subjectDetails = subjectDetailsContainer.subject
    val bookmarks = bookmarkRepository.bookmarks
    val selectedBookmark = _selectedBookmark.asStateFlow()
    val errors = Channel<UiText>()

    private val _renameName = MutableStateFlow<String?>(null)
    val renameName = _renameName.asStateFlow()

    private val _renameAlias = MutableStateFlow("")
    val renameAlias = _renameAlias.asStateFlow()

    init {
        viewModelScope.launch {
            val bookmarks = bookmarks.first { list -> list.isNotEmpty() }
            getSchedule(bookmarks.first())
        }

        viewModelScope.launch {
            loadingState
                .filter { it is UiLoadingState.Error }
                .collect {
                    errors.send((it as UiLoadingState.Error).message)
                }
        }
    }

    fun getSchedule(bookmark: ScheduleBookmark) {
        if (_selectedBookmark.value != bookmark) {
            viewModelScope.launch {
                _selectedBookmark.value = bookmark
                scheduleContainer.update(bookmark.scheduleId.value)
            }
        }
    }

    fun refreshIfNeeded() {
        if (selectedScheduleId.type != ScheduleType.Unknown) {
            scheduleContainer.refreshIfNeeded()
        }
    }

    fun forceSync() {
        scheduleContainer.update(selectedScheduleId.value, SyncType.Force)
    }

    fun cancelSync() {
        scheduleContainer.cancelSync()
    }


    fun openSubjectDetails(subjectId: Long) {
        viewModelScope.launch {
            subjectDetailsContainer.show(subjectId, selectedScheduleId.type)
        }
    }

    fun hideSubjectDetails() {
        subjectDetailsContainer.hide()
    }


    fun startRenaming(name: String, alias: String) {
        _renameName.value = name
        _renameAlias.value = alias
    }

    fun updateNameAlias(alias: String) {
        _renameAlias.value = alias
    }

    fun finishRenaming() {
        val name = _renameName.value ?: return
        val newAlias = _renameAlias.value.trim()

        viewModelScope.launch {
            subjectUseCases.setNameAlias(name, newAlias).ifFailure { errors.send(message) }
            cancelRenaming()
            scheduleContainer.update(selectedScheduleId.value, SyncType.None).join()
            subjectDetailsContainer.subject.value?.let {
                openSubjectDetails(it.subjectId)
            }
        }
    }

    fun cancelRenaming() {
        _renameName.value = null
    }
}
