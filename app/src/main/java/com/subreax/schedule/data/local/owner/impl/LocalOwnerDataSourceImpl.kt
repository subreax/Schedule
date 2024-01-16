package com.subreax.schedule.data.local.owner.impl

import com.subreax.schedule.data.local.ScheduleDatabase
import com.subreax.schedule.data.local.entitiy.LocalOwner
import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalOwnerDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalOwnerDataSource {
    private val ownerDao = database.ownerDao

    override suspend fun addScheduleOwner(owner: String): Boolean {
        return try {
            ownerDao.addOwner(LocalOwner(0, owner))
            true
        } catch (ex: Exception) {
            false
        }
    }

    override fun getScheduleOwners(): Flow<List<LocalOwner>> {
        return ownerDao.getOwners()
    }

    override suspend fun getFirstScheduleOwner(): LocalOwner? {
        return ownerDao.getFirstOwner()
    }

    override suspend fun removeScheduleOwnerByName(ownerName: String) {
        ownerDao.removeOwnerByNetworkId(ownerName)
    }

    override suspend fun updateScheduleOwnerName(id: String, name: String) {
        ownerDao.updateOwnerNameByNetworkId(id, name.trim())
    }
}