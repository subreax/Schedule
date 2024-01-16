package com.subreax.schedule.data.repository.scheduleowner.impl

import com.subreax.schedule.R
import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduleOwnerRepositoryImpl @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val localOwnerDataSource: LocalOwnerDataSource,
    private val networkDataSource: NetworkDataSource
) : ScheduleOwnerRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _scheduleOwners = localOwnerDataSource.getScheduleOwners()
        .map {
            it.map { local -> ScheduleOwner(local.networkId, local.name) }
        }
        .stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

    override fun getScheduleOwners() = _scheduleOwners

    override suspend fun getFirstOwner(): ScheduleOwner? = withContext(Dispatchers.IO) {
        localOwnerDataSource.getFirstScheduleOwner()?.let {
            ScheduleOwner(it.networkId, it.name)
        }
    }

    override suspend fun addScheduleOwner(owner: String) = withContext(Dispatchers.IO) {
        if (networkDataSource.isScheduleOwnerExists(owner)) {
            if (localOwnerDataSource.addScheduleOwner(owner)) {
                Resource.Success(Unit)
            }
            else {
                Resource.Failure(UiText.res(R.string.schedule_id_already_exists))
            }
        }
        else {
            Resource.Failure(UiText.res(R.string.schedule_id_not_found))
        }
    }

    override suspend fun getScheduleOwnerHints(owner: String): List<String> = withContext(Dispatchers.IO) {
        networkDataSource.getScheduleOwnerHints(owner)
    }

    override suspend fun deleteScheduleOwner(scheduleOwner: ScheduleOwner) = withContext(Dispatchers.IO) {
        localOwnerDataSource.deleteScheduleOwnerByName(scheduleOwner.id)
        scheduleRepository.deleteSchedule(scheduleOwner)
    }

    override suspend fun updateScheduleOwnerName(id: String, name: String) = withContext(Dispatchers.IO) {
        localOwnerDataSource.updateScheduleOwnerName(id, name)
    }
}