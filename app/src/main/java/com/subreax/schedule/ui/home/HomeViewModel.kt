package com.subreax.schedule.ui.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.GetScheduleUseCase
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.UiSchedule
import com.subreax.schedule.ui.UiSubjectDetails
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    scheduleRepository: ScheduleRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val getScheduleUseCase = GetScheduleUseCase(scheduleRepository, appContext)

    val bookmarks = bookmarkRepository.bookmarks
    var selectedBookmark by mutableStateOf(ScheduleBookmark(ScheduleId("", ScheduleType.Student), ScheduleBookmark.NO_NAME))
        private set

    val uiSchedule: Flow<UiSchedule> = getScheduleUseCase.schedule
    val uiLoadingState: StateFlow<UiLoadingState> = getScheduleUseCase.uiLoadingState

    var pickedSubject by mutableStateOf<UiSubjectDetails?>(null)
        private set

    val errors = Channel<UiText>()

    init {
        viewModelScope.launch {
            val owners = bookmarks.first { it.isNotEmpty() }
            if (owners.isNotEmpty()) {
                getSchedule(owners.first().scheduleId.value)
            } else {
                errors.send(UiText.hardcoded("Вы не сохранили ни одного расписания"))
            }
        }
    }

    fun getSchedule(ownerNetworkId: String) {
        viewModelScope.launch {
            selectedBookmark = bookmarkRepository.getBookmark(ownerNetworkId).requireValue()
            getScheduleUseCase.init(ownerNetworkId)
        }
    }

    fun openSubjectDetails(subjectId: Long) {
        viewModelScope.launch {
            when (val res = getScheduleUseCase.getSubjectDetails(subjectId)) {
                is Resource.Success ->
                    pickedSubject = res.value

                is Resource.Failure ->
                    errors.send(res.message)
            }
        }
    }

    fun hideSubjectDetails() {
        pickedSubject = null
    }

    fun startRenaming(name: String) {
        Log.d(TAG, "startRenaming: $name")
        viewModelScope.launch {
            //renameUseCase.startRenaming(name)
        }
    }

    fun finishRenaming() {
        Log.d(TAG, "finishRenaming")
        viewModelScope.launch {
            //renameUseCase.finishRenaming()
            //updateSchedule()
        }
    }

    fun cancelRenaming() {
        //renameUseCase.cancelRenaming()
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
