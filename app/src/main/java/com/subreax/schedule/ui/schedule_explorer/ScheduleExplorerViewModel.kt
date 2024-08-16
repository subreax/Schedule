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
    bookmarkRepository: BookmarkRepository
) : ViewModel() {
    val scheduleId = savedStateHandle.get<String>("id")!!.urlDecode()

    private val getScheduleUseCase = GetScheduleUseCase(scheduleRepository, bookmarkRepository, appContext, viewModelScope)

    val uiSchedule = getScheduleUseCase.schedule
    val uiLoadingState = getScheduleUseCase.loadingState

    private val _pickedSubject = MutableStateFlow<UiSubjectDetails?>(null)
    val pickedSubject = _pickedSubject.asStateFlow()

    val errors = Channel<UiText>()

    init {
        viewModelScope.launch {
            getScheduleUseCase.init(scheduleId)

            uiLoadingState
                .filter { it is UiLoadingState.Error }
                .collect {
                    errors.send((it as UiLoadingState.Error).message)
                }
        }
    }

    fun openSubjectDetails(subjectId: Long) {
        viewModelScope.launch {
            when (val res = getScheduleUseCase.getSubjectDetails(subjectId)) {
                is Resource.Success -> _pickedSubject.value = res.value
                is Resource.Failure -> errors.send(res.message)
            }
        }
    }

    fun hideSubjectDetails() {
        _pickedSubject.value = null
    }

    private fun String.urlDecode(): String {
        return URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
    }
}
