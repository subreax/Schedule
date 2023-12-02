package com.subreax.schedule.data.local

import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.local.entitiy.LocalOwner
import com.subreax.schedule.data.local.entitiy.LocalSubject
import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import java.util.Date
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalDataSource {
    private val subjectDao = database.subjectDao
    private val subjectNameDao = database.subjectNameDao
    private val ownerDao = database.ownerDao

    override suspend fun saveSchedule(owner: String, schedule: List<Subject>) {
        subjectDao.deleteSubjects(getOwnerId(owner), Date().time / 60000L)

        subjectDao.insert(
            schedule
                .sortedBy { it.timeRange.start }
                .map {
                    LocalSubject(
                        id = 0,
                        type = it.type.typeId(),
                        ownerId = getOwnerId(owner),
                        nameId = insertSubjectNameIfNotExist(it.name),
                        place = it.place,
                        teacherName = it.teacherName?.full() ?: "",
                        beginTimeMins = it.timeRange.start.time / 60000,
                        endTimeMins = it.timeRange.end.time / 60000
                    )
                })
    }

    private suspend fun insertSubjectNameIfNotExist(name: String): Int {
        subjectNameDao.addNameIfNotExist(LocalSubjectName(0, name, name))
        return subjectNameDao.getNameId(name)
    }

    private suspend fun getOwnerId(name: String): Int {
        return ownerDao.findOwnerByName(name)!!.id
    }

    override suspend fun loadSchedule(owner: String): List<Subject> {
        return subjectDao.findSubjectsByOwnerId(getOwnerId(owner))
            .map { it.toModel() }
    }

    override suspend fun findSubjectById(id: Int): Subject? {
        return subjectDao.findSubjectById(id)?.toModel()
    }

    override suspend fun addOwner(owner: String): Boolean {
        return try {
            ownerDao.addOwner(LocalOwner(0, owner))
            true
        } catch (ex: Exception) {
            false
        }
    }

    override suspend fun getOwners(): List<ScheduleOwner> {
        return ownerDao.getOwners().map {
            ScheduleOwner(it.name)
        }
    }

    private fun LocalExpandedSubject.toModel(): Subject {
        return Subject(
            id = id,
            name = name,
            place = place,
            type = type.toSubjectType(),
            timeRange = TimeRange(
                Date(beginTimeMins * 60000),
                Date(endTimeMins * 60000)
            ),
            teacherName = if (teacher.isNotEmpty()) {
                PersonName.parse(teacher)
            } else {
                null
            }
        )
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

    private fun milliseconds2days(ms: Long): Long {
        return ms / (1000 * 60 * 60 * 24)
    }

    companion object {
        // todo: rename
        // Устанавливает количество дней на будущее,
        // для которых сохраняется расписание
        private const val SAVE_SCHEDULE_FOR_DAYS = 7
    }
}
