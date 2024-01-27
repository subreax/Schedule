package com.subreax.schedule.data.repository.scheduleowner

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.flow.StateFlow

interface ScheduleOwnerRepository {
    fun getOwners(): StateFlow<List<ScheduleOwner>>
    suspend fun getFirstOwner(): ScheduleOwner?
    suspend fun addOwner(networkId: String): Resource<Unit>
    suspend fun getHints(networkId: String): List<String>
    suspend fun deleteOwner(owner: ScheduleOwner): Resource<Unit>
    suspend fun updateOwnerName(networkId: String, name: String)
}