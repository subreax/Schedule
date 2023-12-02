package com.subreax.schedule.data.network

import com.subreax.schedule.data.network.model.NetworkSubject

interface NetworkDataSource {
    suspend fun getGroupSchedule(group: String): List<NetworkSubject>
    suspend fun isScheduleIdExists(scheduleId: String): Boolean
    suspend fun getScheduleIdHints(scheduleId: String): List<String>
}
