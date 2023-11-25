package com.subreax.schedule.data.repository.schedule.impl

import com.subreax.schedule.data.local.LocalDataSource
import com.subreax.schedule.data.model.ScheduleOwner
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
    override suspend fun getScheduleForGroup(group: String): Resource<List<Subject>> {
        return withContext(Dispatchers.Default) {
            try {
                val schedule = fetchScheduleForGroup(group)
                localDataSource.saveSchedule(group, schedule)
                Resource.Success(loadSchedule(group))
            } catch (ex: Exception) {
                val msg = "Не удалось загрузить расписание с сервера: ${ex.message}"
                Resource.Failure(UiText.hardcoded(msg), loadSchedule(group))
            }
        }
    }

    private suspend fun fetchScheduleForGroup(group: String): List<Subject> {
        val now = Date().time
        val networkSchedule = networkDataSource.getGroupSchedule(group)
        return networkSchedule
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

    private suspend fun loadSchedule(owner: String): List<Subject> {
        val now = Date()
        return localDataSource.loadSchedule(owner)
            .filter { it.timeRange.end >= now }
    }

    override suspend fun getScheduleOwners(): List<ScheduleOwner> {
        return listOf(
            ScheduleOwner("220431"),
            ScheduleOwner("620221")
        )
    }

    override suspend fun getLastRequestedScheduleId(): String {
        return "220431"
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