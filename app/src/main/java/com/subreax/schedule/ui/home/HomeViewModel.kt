package com.subreax.schedule.ui.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.ui.BaseScheduleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    scheduleRepository: ScheduleRepository,
    scheduleOwnerRepository: ScheduleOwnerRepository
) : BaseScheduleViewModel(appContext, scheduleRepository) {
    val scheduleOwners = scheduleOwnerRepository.getOwners()

    init {
        viewModelScope.launch {
            scheduleOwners
                .dropWhile { it.isEmpty() }
                .take(1)
                .collect {
                    getSchedule(it.first().networkId)
                }
        }
    }
}
