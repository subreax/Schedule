package com.subreax.schedule.data.local.owner.impl

import com.subreax.schedule.data.local.ScheduleDatabase
import com.subreax.schedule.data.local.entitiy.toLocal
import com.subreax.schedule.data.local.entitiy.toModel
import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class LocalOwnerDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalOwnerDataSource {
    private val ownerDao = database.ownerDao

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _scheduleOwners = ownerDao.getOwners()
        .map {
            it.map { local -> local.toModel() }
        }
        .stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())


    override suspend fun addOwner(owner: ScheduleOwner): Boolean {
        return try {
            ownerDao.addOwner(owner.toLocal())
            true
        } catch (ex: Exception) {
            false
        }
    }

    override fun getOwners(): StateFlow<List<ScheduleOwner>> = _scheduleOwners

    override suspend fun getFirstOwner(): ScheduleOwner? {
        return ownerDao.getFirstOwner()?.toModel()
    }

    override suspend fun deleteOwnerByName(ownerName: String) {
        ownerDao.deleteOwnerByNetworkId(ownerName)
    }

    override suspend fun updateOwnerName(id: String, name: String) {
        ownerDao.updateOwnerNameByNetworkId(id, name.trim())
    }
}
