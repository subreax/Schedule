package com.subreax.schedule.data.local

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject

interface LocalDataSource {
    suspend fun saveSchedule(owner: String, schedule: List<Subject>)
    suspend fun loadSchedule(owner: String): List<Subject>
    suspend fun findSubjectById(id: Int): Subject?
    suspend fun addOwner(owner: String): Boolean
    suspend fun getOwners(): List<ScheduleOwner>
}