package com.subreax.schedule.data.repository.schedule

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface ScheduleRepository {
    suspend fun getScheduleForGroup(group: String): Resource<List<Subject>>
    suspend fun findSubjectById(id: Int): Subject?
    suspend fun getScheduleOwners(): List<ScheduleOwner>
    suspend fun getLastRequestedScheduleId(): String?
    suspend fun addScheduleId(id: String): Resource<Unit>
    suspend fun getScheduleIdHints(id: String): List<String>
}