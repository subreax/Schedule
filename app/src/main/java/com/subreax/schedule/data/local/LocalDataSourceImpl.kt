package com.subreax.schedule.data.local

import com.subreax.schedule.data.local.entitiy.LocalSubject
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import java.util.Date
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalDataSource {
    private val subjectDao = database.subjectDao

    override suspend fun updateSchedule(owner: String, schedule: List<Subject>) {
        subjectDao.upsert(schedule.map {
            LocalSubject(
                owner = owner,
                beginTime = it.timeRange.start.time,
                endTime = it.timeRange.end.time,
                name = it.name,
                type = it.type.typeId(),
                place = it.place,
                teacherFirstName = it.teacherName?.first ?: "",
                teacherLastName = it.teacherName?.last ?: "",
                teacherMiddleName = it.teacherName?.middle ?: ""
            )
        })
    }

    override suspend fun loadSchedule(owner: String): List<Subject> {
        return subjectDao.getLocalSubjectOrderedByBeginTime(owner)
            .map {
                Subject(
                    it.name,
                    it.place,
                    it.type.toSubjectType(),
                    timeRange = TimeRange(Date(it.beginTime), Date(it.endTime)),
                    teacherName = PersonName(
                        it.teacherFirstName,
                        it.teacherLastName,
                        it.teacherMiddleName
                    )
                )
            }
    }

    private fun SubjectType.typeId(): Int {
        return when (this) {
            SubjectType.Lecture -> 0
            SubjectType.Practice -> 1
            SubjectType.Lab -> 2
            SubjectType.Exam -> 3
            else -> error("typeId(): Unknown subject type")
        }
    }

    private fun Int.toSubjectType(): SubjectType {
        return when (this) {
            0 -> SubjectType.Lecture
            1 -> SubjectType.Practice
            2 -> SubjectType.Lab
            3 -> SubjectType.Exam
            else -> error("toSubjectType(): Unknown type id")
        }
    }
}
