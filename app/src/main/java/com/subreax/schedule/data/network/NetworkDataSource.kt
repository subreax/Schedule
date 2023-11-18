package com.subreax.schedule.data.network

import com.subreax.schedule.data.network.model.NetworkSubject

interface NetworkDataSource {
    suspend fun getGroupSchedule(group: String): List<NetworkSubject>
}
