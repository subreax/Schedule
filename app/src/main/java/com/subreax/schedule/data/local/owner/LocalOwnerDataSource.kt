package com.subreax.schedule.data.local.owner

import com.subreax.schedule.data.local.entitiy.LocalOwner
import kotlinx.coroutines.flow.Flow

interface LocalOwnerDataSource {
    suspend fun addScheduleOwner(owner: String): Boolean
    fun getScheduleOwners(): Flow<List<LocalOwner>>
    suspend fun getFirstScheduleOwner(): LocalOwner?
    suspend fun removeScheduleOwnerByName(ownerName: String)
    suspend fun updateScheduleOwnerName(id: String, name: String)
}