package com.subreax.schedule.data.local

import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject

interface LocalDataSource {
    suspend fun saveSchedule(scheduleOwner: String, schedule: List<Subject>)
    suspend fun loadSchedule(scheduleOwner: String): List<LocalExpandedSubject>
    suspend fun findSubjectById(id: Int): LocalExpandedSubject?
    suspend fun addScheduleOwner(owner: String): Boolean
    suspend fun getScheduleOwners(): List<ScheduleOwner>
}