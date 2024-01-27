package com.subreax.schedule.data.network.owner.impl

import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.network.owner.NetworkOwnerDataSource
import javax.inject.Inject

class NetworkOwnerDataSourceImpl @Inject constructor(
    private val service: RetrofitService
) : NetworkOwnerDataSource {
    override suspend fun getOwnerHints(ownerId: String): List<String> {
        return service.getDictionaries(ownerId).map { it.value }
    }

    override suspend fun isOwnerExist(ownerId: String): Boolean {
        return service.getDates(ownerId).error == null
    }

    override suspend fun getOwnerType(ownerId: String): String? {
        val info = service.getDates(ownerId)
        return if (info.error != null) {
            null
        } else {
            info.scheduleOwnerType
        }
    }
}