package com.subreax.schedule.data.local

import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.local.entitiy.LocalOwner
import com.subreax.schedule.data.model.Subject
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun saveSchedule(scheduleOwner: String, schedule: List<Subject>)
    suspend fun loadSchedule(scheduleOwner: String): List<LocalExpandedSubject>
    suspend fun findSubjectById(id: Int): LocalExpandedSubject?
    suspend fun addScheduleOwner(owner: String): Boolean
    fun getScheduleOwners(): Flow<List<LocalOwner>>
    suspend fun getFirstScheduleOwner(): LocalOwner?
    suspend fun removeScheduleOwnerByName(ownerName: String)
    suspend fun updateScheduleOwnerName(id: String, name: String)
}