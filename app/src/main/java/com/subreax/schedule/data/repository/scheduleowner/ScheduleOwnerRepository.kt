package com.subreax.schedule.data.repository.scheduleowner

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.utils.Resource

interface ScheduleOwnerRepository {
    suspend fun getScheduleOwners(): List<ScheduleOwner>
    suspend fun addScheduleOwner(owner: String): Resource<Unit>
    suspend fun getScheduleOwnerHints(owner: String): List<String>
}