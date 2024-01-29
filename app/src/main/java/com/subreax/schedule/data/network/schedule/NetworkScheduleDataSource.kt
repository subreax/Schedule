package com.subreax.schedule.data.network.schedule

import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.Subject

interface NetworkScheduleDataSource {
    suspend fun getSubjects(owner: String, type: String, minEndTime: Long): List<Subject>
    suspend fun getSchedule(ownerId: String, minEndTime: Long): Schedule?
}
