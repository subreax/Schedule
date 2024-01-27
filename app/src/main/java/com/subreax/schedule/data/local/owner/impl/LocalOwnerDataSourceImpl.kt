package com.subreax.schedule.data.local.owner.impl

import com.subreax.schedule.data.local.ScheduleDatabase
import com.subreax.schedule.data.local.entitiy.LocalOwner
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

    private val _owners = ownerDao.getOwners()
        .map {
            it.map { local -> local.toModel() }
        }
        .stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())


    override fun getOwners(): StateFlow<List<ScheduleOwner>> = _owners

    override suspend fun addOwner(owner: ScheduleOwner): Boolean {
        return try {
            ownerDao.addOwner(owner.toLocal())
            true
        } catch (ex: Exception) {
            false
        }
    }

    override suspend fun getFirstOwner(): ScheduleOwner? {
        return ownerDao.getFirstOwner()?.toModel()
    }

    override suspend fun deleteOwnerByName(name: String) {
        ownerDao.deleteOwnerByNetworkId(name)
    }

    override suspend fun updateOwnerName(networkId: String, name: String) {
        ownerDao.updateOwnerNameByNetworkId(networkId, name.trim())
    }
}

private fun ScheduleOwner.toLocal(localId: Int = 0): LocalOwner {
    return LocalOwner(localId, this.networkId, this.type.ordinal, this.name)
}

private fun LocalOwner.toModel(): ScheduleOwner {
    val type = ScheduleOwner.Type.entries[this.typeValue]
    return ScheduleOwner(this.networkId, type, name)
}
