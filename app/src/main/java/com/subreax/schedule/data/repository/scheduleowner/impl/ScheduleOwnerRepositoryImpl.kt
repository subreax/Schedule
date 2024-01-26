package com.subreax.schedule.data.repository.scheduleowner.impl

import com.subreax.schedule.R
import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.network.toScheduleOwnerType
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
    private val networkDataSource: NetworkDataSource
) : ScheduleOwnerRepository {
    override fun getScheduleOwners() = localOwnerDataSource.getOwners()

    override suspend fun getFirstOwner(): ScheduleOwner? {
        return withContext(Dispatchers.IO) {
            localOwnerDataSource.getFirstOwner()
        }
    }

    override suspend fun addScheduleOwner(owner: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            val type = getOwnerType(owner)
            if (type != null) {
                if (localOwnerDataSource.addOwner(ScheduleOwner(owner, type, ""))) {
                    Resource.Success(Unit)
                } else {
                    Resource.Failure(UiText.res(R.string.schedule_id_already_exists))
                }
            } else {
                Resource.Failure(UiText.res(R.string.schedule_id_not_found))
            }
        }
    }

    override suspend fun getScheduleOwnerHints(owner: String): List<String> {
        return withContext(Dispatchers.IO) {
            networkDataSource.getScheduleOwnerHints(owner)
        }
    }

    override suspend fun deleteScheduleOwner(scheduleOwner: ScheduleOwner): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            localOwnerDataSource.deleteOwnerByName(scheduleOwner.id)
            scheduleRepository.deleteSchedule(scheduleOwner)
        }
    }

    override suspend fun updateScheduleOwnerName(id: String, name: String) {
        return withContext(Dispatchers.IO) {
            localOwnerDataSource.updateOwnerName(id, name)
        }
    }

    private suspend fun getOwnerType(networkId: String): ScheduleOwner.Type? {
        val networkType = networkDataSource.getOwnerType(networkId)
        return networkType?.toScheduleOwnerType()
    }
}