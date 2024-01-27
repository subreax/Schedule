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

    override suspend fun addOwner(networkId: String): Resource<Unit> {
        return withContext(Dispatchers.Default) {
            val type = getOwnerType(networkId)
            if (type != null) {
                if (localOwnerDataSource.addOwner(ScheduleOwner(networkId, type, ""))) {
                    Resource.Success(Unit)
                } else {
                    Resource.Failure(UiText.res(R.string.schedule_id_already_exists))
                }
            } else {
                Resource.Failure(UiText.res(R.string.schedule_id_not_found))
            }
        }
    }

    override suspend fun getHints(networkId: String): List<String> {
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

    private suspend fun getOwnerType(networkId: String): ScheduleOwner.Type? {
        val networkType = networkOwnerDataSource.getOwnerType(networkId)
        return networkType?.toScheduleOwnerType()
    }
}