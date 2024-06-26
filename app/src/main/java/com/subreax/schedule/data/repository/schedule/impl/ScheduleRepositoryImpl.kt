package com.subreax.schedule.data.repository.schedule.impl

import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.local.subjectname.LocalSubjectNameDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.network.owner.NetworkOwnerDataSource
import com.subreax.schedule.data.network.owner.toScheduleOwnerType
import com.subreax.schedule.data.network.schedule.NetworkScheduleDataSource
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule.provider.CachedScheduleProvider
import com.subreax.schedule.data.repository.schedule.provider.NetworkScheduleProvider
import com.subreax.schedule.data.repository.schedule.provider.ScheduleProvider
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val networkScheduleDataSource: NetworkScheduleDataSource,
    private val localScheduleDataSource: LocalScheduleDataSource,
    private val networkOwnerDataSource: NetworkOwnerDataSource,
    private val localOwnerDataSource: LocalOwnerDataSource
) : ScheduleRepository {
    override suspend fun getScheduleProvider(ownerNetworkId: String): Resource<ScheduleProvider> {
        val localOwner = getLocalOwnerByNetworkId(ownerNetworkId)
        if (localOwner != null) {
            val provider = CachedScheduleProvider(
                localOwner,
                localOwnerDataSource,
                localScheduleDataSource,
                networkScheduleDataSource
            )
            return Resource.Success(provider)
        }

        return getNetworkOwnerById(ownerNetworkId)
            .mapResult { NetworkScheduleProvider(it, networkScheduleDataSource) }
    }

    private fun getLocalOwnerByNetworkId(ownerNetworkId: String): ScheduleOwner? {
        return localOwnerDataSource.getOwners().value
            .find { it.networkId == ownerNetworkId }
    }

    private suspend fun getNetworkOwnerById(networkId: String): Resource<ScheduleOwner> {
        return networkOwnerDataSource.getOwnerType(networkId)
            .mapResult { ScheduleOwner(networkId, it.toScheduleOwnerType(), "") }
    }

    override suspend fun deleteSchedule(owner: ScheduleOwner): Resource<Unit> {
        return withContext(Dispatchers.Default) {
            localScheduleDataSource.deleteSchedule(owner.networkId)
        }
    }
}