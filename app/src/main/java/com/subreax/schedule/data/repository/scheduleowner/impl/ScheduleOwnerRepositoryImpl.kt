package com.subreax.schedule.data.repository.scheduleowner.impl

import com.subreax.schedule.R
import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.network.owner.NetworkOwnerDataSource
import com.subreax.schedule.data.network.owner.toScheduleOwnerType
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduleOwnerRepositoryImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val localOwnerDataSource: LocalOwnerDataSource,
    private val networkOwnerDataSource: NetworkOwnerDataSource
) : ScheduleOwnerRepository {
    override fun getOwners() = localOwnerDataSource.getOwners()

    override suspend fun getFirstOwner(): ScheduleOwner? {
        return withContext(Dispatchers.Default) {
            localOwnerDataSource.getFirstOwner()
        }
    }

    override suspend fun getLocalOwnerByNetworkId(networkId: String): ScheduleOwner? {
        return withContext(Dispatchers.Default) {
            localOwnerDataSource.getOwners().value.find { it.networkId == networkId }
        }
    }

    override suspend fun getNetworkOwnerById(networkId: String): Resource<ScheduleOwner> {
        return withContext(Dispatchers.Default) {
            networkOwnerDataSource.getOwnerType(networkId)
                .mapResult { ScheduleOwner(networkId, it.toScheduleOwnerType(), "") }
        }
    }

    override suspend fun addOwner(networkId: String): Resource<Unit> {
        return withContext(Dispatchers.Default) {
            val typeRes = getOwnerType(networkId)
            if (typeRes is Resource.Failure) {
                return@withContext Resource.Failure(typeRes.message)
            }

            val type = (typeRes as Resource.Success).value
            if (localOwnerDataSource.addOwner(ScheduleOwner(networkId, type, ""))) {
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.res(R.string.schedule_id_already_exists))
            }
        }
    }

    override suspend fun getHints(networkId: String): Resource<List<String>> {
        return withContext(Dispatchers.Default) {
            networkOwnerDataSource.getOwnerHints(networkId)
        }
    }

    override suspend fun deleteOwner(owner: ScheduleOwner): Resource<Unit> {
        return withContext(Dispatchers.Default) {
            localOwnerDataSource.deleteOwnerByName(owner.networkId)
            scheduleRepository.deleteSchedule(owner)
        }
    }

    override suspend fun updateOwnerName(networkId: String, name: String) {
        return withContext(Dispatchers.Default) {
            localOwnerDataSource.updateOwnerName(networkId, name)
        }
    }

    private suspend fun getOwnerType(networkId: String): Resource<ScheduleOwner.Type> {
        return networkOwnerDataSource.getOwnerType(networkId)
            .mapResult { it.toScheduleOwnerType() }
    }
}
