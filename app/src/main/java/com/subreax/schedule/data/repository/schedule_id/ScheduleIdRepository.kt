package com.subreax.schedule.data.repository.schedule_id

import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.utils.Resource

interface ScheduleIdRepository {
    suspend fun getScheduleId(id: String): Resource<ScheduleId>
    suspend fun getScheduleIds(startsWith: String = ""): Resource<List<ScheduleId>>
    suspend fun isScheduleIdExist(id: String): Resource<Boolean>
}