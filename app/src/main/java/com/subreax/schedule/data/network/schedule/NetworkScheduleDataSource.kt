package com.subreax.schedule.data.network.schedule

import com.subreax.schedule.data.model.Subject

interface NetworkScheduleDataSource {
    suspend fun getSubjects(owner: String, type: String, minEndTime: Long): List<Subject>
}
