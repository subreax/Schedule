package com.subreax.schedule.data.local.schedule.impl

import android.util.Log
import com.subreax.schedule.data.local.ScheduleDatabase
import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.local.entitiy.LocalSubject
import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.local.entitiy.LocalTeacherName
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.toMilliseconds
import com.subreax.schedule.utils.toMinutes
import java.util.Date
import javax.inject.Inject

class LocalScheduleDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalScheduleDataSource {
    private val subjectDao = database.subjectDao
    private val ownerDao = database.ownerDao
    private val subjectNameDao = database.subjectNameDao
    private val teacherNameDao = database.teacherNameDao

    override suspend fun insertSubjects(ownerNetworkId: String, schedule: List<Subject>) {
        val ownerId = getLocalOwnerId(ownerNetworkId)
        if (ownerId == null) {
            Log.e(TAG, "Unknown schedule owner: '$ownerNetworkId'")
            return
        }

        subjectDao.insert(schedule
            .map { it.toLocal(ownerId) }
        )
    }

    override suspend fun deleteSubjectsAfterSpecifiedTime(
        ownerNetworkId: String,
        minSubjectEndTime: Long
    ) {
        val ownerId = getLocalOwnerId(ownerNetworkId)
        if (ownerId == null) {
            Log.e(TAG, "Unknown schedule owner: '$ownerNetworkId'")
            return
        }

        subjectDao.deleteSubjectsAfterSpecifiedTime(ownerId, minSubjectEndTime.toMinutes())
    }

    private suspend fun getLocalOwnerId(ownerNetworkId: String): Int? {
        return ownerDao.findOwnerByNetworkId(ownerNetworkId)?.localId
    }

    override suspend fun loadSchedule(
        ownerNetworkId: String,
        minSubjectEndTime: Long
    ): Resource<List<Subject>> {
        val localOwnerId = getLocalOwnerId(ownerNetworkId)
        val minSubjectEndTimeMins = minSubjectEndTime.toMinutes()
        return if (localOwnerId != null) {
            val subjects = subjectDao.findSubjectsByOwnerId(localOwnerId)
                .filter { it.endTimeMins >= minSubjectEndTimeMins }
                .map { it.toModel() }
            Resource.Success(subjects)
        } else {
            Resource.Failure(UiText.hardcoded("Неизвестный идентификатор расписания"))
        }
    }

    override suspend fun deleteSchedule(ownerNetworkId: String): Resource<Unit> {
        val ownerId = getLocalOwnerId(ownerNetworkId)
        return if (ownerId != null) {
            subjectDao.deleteSubjects(ownerId)
            Resource.Success(Unit)
        } else {
            Resource.Failure(UiText.hardcoded("Неизвестный идентификатор расписания"))
        }
    }

    override suspend fun findSubjectById(id: Long): Subject? {
        return subjectDao.findSubjectById(id)?.toModel()
    }

    override suspend fun hasSubjects(ownerNetworkId: String): Boolean {
        val localOwnerId = getLocalOwnerId(ownerNetworkId) ?: return false
        return subjectDao.countSubjects(localOwnerId) > 0
    }

    private suspend fun Subject.toLocal(localOwnerId: Int): LocalSubject {
        val beginTimeMins = timeRange.start.time.toMinutes()
        val subjectNameId = insertSubjectNameIfNotExist(name)
        val teacherNameId = insertTeacherNameIfNotExist(teacher?.full() ?: "")
        val id = LocalSubject.buildId(localOwnerId, beginTimeMins, subjectNameId, teacherNameId)

        return LocalSubject(
            id = id,
            typeId = type.id,
            ownerId = localOwnerId,
            subjectNameId = subjectNameId,
            place = place,
            teacherNameId = teacherNameId,
            beginTimeMins = beginTimeMins,
            endTimeMins = timeRange.end.time.toMinutes(),
            rawGroups = LocalSubject.buildRawGroups(groups)
        )
    }

    private suspend fun insertSubjectNameIfNotExist(name: String): Int {
        subjectNameDao.addNameIfNotExist(LocalSubjectName(0, name, name))
        return subjectNameDao.getNameId(name)
    }

    private suspend fun insertTeacherNameIfNotExist(name: String): Int {
        teacherNameDao.addNameIfNotExist(LocalTeacherName(0, name))
        return teacherNameDao.getNameId(name)
    }

    private fun LocalExpandedSubject.toModel(): Subject {
        return Subject(
            id = id,
            name = name,
            place = place,
            type = SubjectType.fromId(typeId),
            timeRange = TimeRange(
                Date(beginTimeMins.toLong().toMilliseconds()),
                Date(endTimeMins.toLong().toMilliseconds())
            ),
            teacher = if (teacher.isNotEmpty()) {
                PersonName.parse(teacher)
            } else {
                null
            },
            groups = LocalSubject.parseGroups(rawGroups)
        )
    }

    companion object {
        private const val TAG = "LocalScheduleDataSource"
    }
}
