package com.subreax.schedule.data.repository.schedule

import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface ScheduleRepository {
    suspend fun getSchedule(owner: String): Resource<List<Subject>>
    suspend fun findSubjectById(id: Int): Subject?
    suspend fun getLastRequestedScheduleOwner(): String?
}