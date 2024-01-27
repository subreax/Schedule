package com.subreax.schedule.data.repository.schedule.impl

import android.util.Log
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.network.owner.networkType
import com.subreax.schedule.data.network.schedule.NetworkScheduleDataSource
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val networkScheduleDataSource: NetworkScheduleDataSource,
    private val localScheduleDataSource: LocalScheduleDataSource
) : ScheduleRepository {
    // todo: add pagination?
    override suspend fun getSchedule(owner: ScheduleOwner): Resource<Schedule> {
        return withContext(Dispatchers.Default) {
            val minSubjectEndTime = System.currentTimeMillis()
            try {
                updateSchedule(owner, minSubjectEndTime)
                val schedule = loadSchedule(owner, minSubjectEndTime)
                Resource.Success(schedule)
            } catch (ex: Exception) {
                val msg = "Не удалось загрузить расписание с сервера: ${ex.message}"
                val schedule = loadSchedule(owner, minSubjectEndTime)
                Resource.Failure(UiText.hardcoded(msg), schedule)
            }
        }
    }

    private suspend fun updateSchedule(owner: ScheduleOwner, minSubjectEndTime: Long) {
        val schedule = fetchSchedule(owner = owner, minSubjectEndTime = minSubjectEndTime)
        if (schedule.subjects.isNotEmpty()) {
            localScheduleDataSource.updateSchedule(owner.networkId, schedule.subjects)
        } else {
            Log.w("ScheduleRepositoryImpl", "Schedule for ${owner.networkId} is empty")
        }
    }

    private suspend fun fetchSchedule(owner: ScheduleOwner, minSubjectEndTime: Long): Schedule {
        val subjects = networkScheduleDataSource.getSubjects(
            owner.networkId,
            owner.networkType,
            minSubjectEndTime
        )

        return Schedule(owner, subjects)
    }

    private suspend fun loadSchedule(owner: ScheduleOwner, minSubjectEndTime: Long): Schedule {
        // todo
        val subjects = (localScheduleDataSource.loadSchedule(owner.networkId, minSubjectEndTime)
                as Resource.Success).value

        return Schedule(owner, subjects)
    }

    override suspend fun deleteSchedule(owner: ScheduleOwner): Resource<Unit> {
        return withContext(Dispatchers.Default) {
            localScheduleDataSource.deleteSchedule(owner.networkId)
        }
    }

    override suspend fun findSubjectById(id: Long): Subject? {
        return withContext(Dispatchers.Default) {
            localScheduleDataSource.findSubjectById(id)
        }
    }
}