package com.subreax.schedule.ui.scheduleexplorer

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleExplorerViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    savedStateHandle: SavedStateHandle,
    scheduleRepository: ScheduleRepository
) : ViewModel() {
    val ownerId = savedStateHandle.get<String>("ownerId")!!

    private val getScheduleUseCase = GetScheduleUseCase(scheduleRepository, appContext)

    val uiSchedule: Flow<UiSchedule> = getScheduleUseCase.schedule
    val uiLoadingState: StateFlow<UiLoadingState> = getScheduleUseCase.uiLoadingState

    var pickedSubject by mutableStateOf<UiSubjectDetails?>(null)
        private set

    val errors = Channel<UiText>()

    init {
        viewModelScope.launch {
            getScheduleUseCase.init(ownerId)

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
                is Resource.Success -> pickedSubject = res.value
                is Resource.Failure -> errors.send(res.message)
            }
        }
    }

    fun hideSubjectDetails() {
        pickedSubject = null
    }
}
