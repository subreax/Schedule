package com.subreax.schedule.data.schedule

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject

interface ScheduleRepository {
    suspend fun getScheduleForGroup(group: String): List<Subject>
    suspend fun getScheduleOwners(): List<ScheduleOwner>
    suspend fun getLastRequestedScheduleId(): String
}