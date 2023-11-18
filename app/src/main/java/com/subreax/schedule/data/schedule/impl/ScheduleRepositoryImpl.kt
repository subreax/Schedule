package com.subreax.schedule.data.schedule.impl

import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.schedule.ScheduleRepository
import java.util.Date
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource
) : ScheduleRepository {
    override suspend fun getScheduleForGroup(group: String): List<Subject> {
        val networkSchedule = networkDataSource.getGroupSchedule(group)

        val now = Date()

        return networkSchedule
            .filter { it.endTime >= now }
            .map {
                Subject(
                    name = it.name,
                    place = it.place,
                    timeRange = TimeRange(it.beginTime, it.endTime),
                    teacherName = it.teacher,
                    type = typeFrom(it.type),
                )
            }
            .sortedBy { it.timeRange.start }
    }

    private fun typeFrom(str: String): SubjectType {
        return when (str) {
            "lecture" -> SubjectType.Lecture
            "practice" -> SubjectType.Practice
            "lab" -> SubjectType.Lab
            else -> SubjectType.Unknown
        }
    }
}