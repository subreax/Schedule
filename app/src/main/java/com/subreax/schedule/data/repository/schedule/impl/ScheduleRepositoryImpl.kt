package com.subreax.schedule.data.repository.schedule.impl

import com.subreax.schedule.data.local.LocalDataSource
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource
) : ScheduleRepository {
    // todo: add pagination?
    override suspend fun getSchedule(owner: String): Resource<List<Subject>> {
        return withContext(Dispatchers.Default) {
            try {
                val schedule = fetchScheduleForGroup(owner)
                localDataSource.saveSchedule(owner, schedule)
                Resource.Success(loadSchedule(owner))
            } catch (ex: Exception) {
                val msg = "Не удалось загрузить расписание с сервера: ${ex.message}"
                Resource.Failure(UiText.hardcoded(msg), loadSchedule(owner))
            }
        }
    }

    private suspend fun fetchScheduleForGroup(group: String): List<Subject> {
        return withContext(Dispatchers.IO) {
            val now = Date().time
            val networkSchedule = networkDataSource.getSchedule(group)
            networkSchedule
                .filter { it.endTime.time >= now }
                .map {
                    Subject(
                        id = 0,
                        name = it.name,
                        place = it.place,
                        timeRange = TimeRange(it.beginTime, it.endTime),
                        teacherName = it.teacher,
                        type = typeFrom(it.type),
                    )
                }
                .sortedBy { it.timeRange.start.time }
        }
    }

    override suspend fun getLastRequestedScheduleOwner(): String? = withContext(Dispatchers.IO) {
        // todo
        localDataSource.getScheduleOwners().firstOrNull()?.id
    }

    private suspend fun loadSchedule(owner: String): List<Subject> {
        return withContext(Dispatchers.IO) {
            val now = Date()
            localDataSource.loadSchedule(owner)
                .filter { it.timeRange.end >= now }
        }
    }

    override suspend fun findSubjectById(id: Int): Subject? = withContext(Dispatchers.IO) {
        localDataSource.findSubjectById(id)
    }


    private fun typeFrom(str: String): SubjectType {
        return when (str) {
            "lecture" -> SubjectType.Lecture
            "practice" -> SubjectType.Practice
            "lab" -> SubjectType.Lab
            "default" -> SubjectType.Exam
            else -> SubjectType.Unknown
        }
    }
}