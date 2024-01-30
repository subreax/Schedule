package com.subreax.schedule.ui.scheduleexplorer

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.BaseScheduleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleExplorerViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    savedStateHandle: SavedStateHandle,
    scheduleRepository: ScheduleRepository
) : BaseScheduleViewModel(appContext, scheduleRepository) {
    val ownerId = savedStateHandle.get<String>("ownerId")!!

    init {
        viewModelScope.launch {
            getSchedule(ownerId)
        }
    }
}
