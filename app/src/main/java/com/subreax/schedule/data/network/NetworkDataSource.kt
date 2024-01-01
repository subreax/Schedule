package com.subreax.schedule.data.network

import com.subreax.schedule.data.network.model.NetworkSubject

interface NetworkDataSource {
    suspend fun getSchedule(scheduleOwner: String): List<NetworkSubject>
    suspend fun isScheduleOwnerExists(scheduleOwner: String): Boolean
    suspend fun getScheduleOwnerHints(scheduleOwner: String): List<String>
}
