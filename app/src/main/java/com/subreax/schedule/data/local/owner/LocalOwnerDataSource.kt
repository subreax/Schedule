package com.subreax.schedule.data.local.owner

import com.subreax.schedule.data.model.ScheduleOwner
import kotlinx.coroutines.flow.StateFlow

interface LocalOwnerDataSource {
    suspend fun addOwner(owner: ScheduleOwner): Boolean
    fun getOwners(): StateFlow<List<ScheduleOwner>>
    suspend fun getFirstOwner(): ScheduleOwner?
    suspend fun deleteOwnerByName(ownerName: String)
    suspend fun updateOwnerName(id: String, name: String)
}