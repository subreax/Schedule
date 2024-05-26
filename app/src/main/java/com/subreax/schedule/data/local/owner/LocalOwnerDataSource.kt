package com.subreax.schedule.data.local.owner

import com.subreax.schedule.data.model.ScheduleOwner
import kotlinx.coroutines.flow.StateFlow

interface LocalOwnerDataSource {
    fun getOwners(): StateFlow<List<ScheduleOwner>>
    suspend fun addOwner(owner: ScheduleOwner): Boolean
    suspend fun getFirstOwner(): ScheduleOwner?
    suspend fun deleteOwnerByName(name: String)
    suspend fun updateOwnerName(networkId: String, name: String)
    suspend fun getScheduleLastUpdateTime(networkId: String): Long
    suspend fun setScheduleLastUpdateTime(networkId: String, lastUpdateTime: Long)
}