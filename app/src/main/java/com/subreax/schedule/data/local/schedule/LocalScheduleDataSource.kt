package com.subreax.schedule.data.local.schedule

import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface LocalScheduleDataSource {
    suspend fun updateSchedule(networkOwnerId: String, schedule: List<Subject>)
    suspend fun loadSchedule(ownerNetworkId: String, minSubjectEndTime: Long): Resource<List<Subject>>
    suspend fun deleteSchedule(ownerNetworkId: String): Resource<Unit>
    suspend fun findSubjectById(id: Long): Subject?
}