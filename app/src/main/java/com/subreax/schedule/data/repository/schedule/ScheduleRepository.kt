package com.subreax.schedule.data.repository.schedule

import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.utils.Resource
import java.util.Date

interface ScheduleRepository {
    suspend fun sync(id: String): Resource<Unit>
    suspend fun get(id: String): Resource<Schedule>
    suspend fun getSyncTime(id: String): Date
    suspend fun clear(id: String): Resource<Unit>
}