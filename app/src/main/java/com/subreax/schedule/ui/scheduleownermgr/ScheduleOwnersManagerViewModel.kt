package com.subreax.schedule.ui.scheduleownermgr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleOwnersManagerViewModel @Inject constructor(
    private val scheduleOwnerRepository: ScheduleOwnerRepository
) : ViewModel() {
    val owners = scheduleOwnerRepository.getScheduleOwners()

    fun removeOwner(scheduleOwner: ScheduleOwner) {
        viewModelScope.launch {
            scheduleOwnerRepository.removeScheduleOwner(scheduleOwner)
        }
    }
}
