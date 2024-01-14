package com.subreax.schedule.data.repository.scheduleowner

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.flow.StateFlow

interface ScheduleOwnerRepository {
    fun getScheduleOwners(): StateFlow<List<ScheduleOwner>>
    suspend fun getFirstOwner(): ScheduleOwner?
    suspend fun addScheduleOwner(owner: String): Resource<Unit>
    suspend fun getScheduleOwnerHints(owner: String): List<String>
    suspend fun removeScheduleOwner(scheduleOwner: ScheduleOwner)
}