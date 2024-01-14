package com.subreax.schedule.data.repository.scheduleowner.impl

import com.subreax.schedule.R
import com.subreax.schedule.data.local.LocalDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.network.NetworkDataSource
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
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) : ScheduleOwnerRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _scheduleOwners = localDataSource.getScheduleOwners()
        .map {
            it.map { local -> ScheduleOwner(local.name) }
        }
        .stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

    override fun getScheduleOwners() = _scheduleOwners

    override suspend fun getFirstOwner(): ScheduleOwner? {
        return localDataSource.getFirstScheduleOwner()?.let {
            ScheduleOwner(it.name)
        }
    }

    override suspend fun addScheduleOwner(owner: String) = withContext(Dispatchers.IO) {
        if (networkDataSource.isScheduleOwnerExists(owner)) {
            if (localDataSource.addScheduleOwner(owner)) {
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

    override suspend fun removeScheduleOwner(scheduleOwner: ScheduleOwner) {
        localDataSource.removeScheduleOwnerByName(scheduleOwner.id)
    }
}