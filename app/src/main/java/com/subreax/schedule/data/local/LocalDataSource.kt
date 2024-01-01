package com.subreax.schedule.data.local

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject

interface LocalDataSource {
    suspend fun saveSchedule(scheduleOwner: String, schedule: List<Subject>)
    suspend fun loadSchedule(scheduleOwner: String): List<Subject>
    suspend fun findSubjectById(id: Int): Subject?
    suspend fun addScheduleOwner(owner: String): Boolean
    suspend fun getScheduleOwners(): List<ScheduleOwner>
}