package com.subreax.schedule.data.repository.schedule.provider

import android.util.Log
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.network.owner.networkType
import com.subreax.schedule.data.network.schedule.NetworkScheduleDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Provides a schedule that will be cached in a local database
 *  after receiving it from the network. Passed schedule owner should exist in the database */
class CachedScheduleProvider(
    private val owner: ScheduleOwner,
    private val localScheduleDataSource: LocalScheduleDataSource,
    private val networkScheduleDataSource: NetworkScheduleDataSource
) : ScheduleProvider {
    private var _cachedSubjects = listOf<Subject>()

    override fun getOwner() = owner

    override suspend fun getSubjects(): Resource<List<Subject>> {
        return withContext(Dispatchers.Default) {
            val minSubjectEndTime = System.currentTimeMillis()
            try {
                updateSchedule(owner, minSubjectEndTime)
                val subjects = loadSubjects(owner, minSubjectEndTime)
                _cachedSubjects = subjects
                Resource.Success(subjects)
            } catch (ex: Exception) {
                val msg = "Не удалось загрузить расписание с сервера: ${ex.message}"
                val subjects = loadSubjects(owner, minSubjectEndTime)
                _cachedSubjects = subjects
                Resource.Failure(UiText.hardcoded(msg), subjects)
            }
        }
    }

    override suspend fun getSubjectById(id: Long): Subject? {
        return withContext(Dispatchers.Default) {
            localScheduleDataSource.findSubjectById(id)
        }
    }

    private suspend fun updateSchedule(owner: ScheduleOwner, minSubjectEndTime: Long) {
        val subjects = networkScheduleDataSource.getSubjects(
            owner.networkId,
            owner.networkType,
            minSubjectEndTime
        )

        if (subjects.isNotEmpty()) {
            localScheduleDataSource.updateSchedule(owner.networkId, subjects)
        } else {
            Log.w("ScheduleRepositoryImpl", "Schedule for ${owner.networkId} is empty")
        }
    }

    private suspend fun loadSubjects(owner: ScheduleOwner, minSubjectEndTime: Long): List<Subject> {
        // todo
        val subjects = (localScheduleDataSource.loadSchedule(owner.networkId, minSubjectEndTime)
                as Resource.Success).value

        return subjects
    }
}