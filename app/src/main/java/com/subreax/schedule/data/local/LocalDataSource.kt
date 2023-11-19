package com.subreax.schedule.data.local

import com.subreax.schedule.data.model.Subject

interface LocalDataSource {
    suspend fun updateSchedule(owner: String, schedule: List<Subject>)
    suspend fun loadSchedule(owner: String): List<Subject>
}