package com.subreax.schedule.data.local.schedule.impl

import android.util.Log
import com.subreax.schedule.data.local.ScheduleDatabase
import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.local.entitiy.LocalSubject
import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.toMinutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalScheduleDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalScheduleDataSource {
    private val subjectDao = database.subjectDao
    private val subjectNameDao = database.subjectNameDao
    private val ownerDao = database.ownerDao

    override suspend fun updateSchedule(scheduleOwner: String, schedule: List<Subject>) {
        return withContext(Dispatchers.IO) {
            val ownerId = getOwnerId(scheduleOwner)
            if (ownerId == null) {
                Log.e(TAG, "Unknown schedule owner: '$scheduleOwner'")
                return@withContext
            }

            deleteSubjectsAfterSpecifiedTime(ownerId, System.currentTimeMillis())

            subjectDao.insert(
                schedule
                    .sortedBy { it.timeRange.start }
                    .map { it.toLocal(ownerId) })
        }
    }

    private suspend fun deleteSubjectsAfterSpecifiedTime(ownerId: Int, time: Long) {
        subjectDao.deleteSubjectsAfterSpecifiedTime(
            ownerId, time.toMinutes()
        )
    }

    private suspend fun insertSubjectNameIfNotExist(name: String): Int {
        return withContext(Dispatchers.IO) {
            subjectNameDao.addNameIfNotExist(LocalSubjectName(0, name, name))
            subjectNameDao.getNameId(name)
        }
    }

    private suspend fun getOwnerId(name: String): Int? {
        return ownerDao.findOwnerByNetworkId(name)?.id
    }

    override suspend fun loadSchedule(scheduleOwner: String): Resource<List<LocalExpandedSubject>> {
        return withContext(Dispatchers.IO) {
            val ownerId = getOwnerId(scheduleOwner)
            if (ownerId != null) {
                val subjects = subjectDao.findSubjectsByOwnerId(ownerId)
                Resource.Success(subjects)
            } else {
                Resource.Failure(UiText.hardcoded("Неизвестный идентификатор расписания"))
            }
        }
    }

    override suspend fun deleteSchedule(scheduleOwner: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            val ownerId = getOwnerId(scheduleOwner)
            if (ownerId != null) {
                subjectDao.deleteSubjects(ownerId)
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.hardcoded("Неизвестный идентификатор расписания"))
            }
        }
    }

    override suspend fun findSubjectById(id: Int): LocalExpandedSubject? {
        return withContext(Dispatchers.IO) {
            subjectDao.findSubjectById(id)
        }
    }

    private suspend fun Subject.toLocal(ownerId: Int): LocalSubject {
        return LocalSubject(
            id = 0,
            typeId = type.id,
            ownerId = ownerId,
            nameId = insertSubjectNameIfNotExist(name),
            place = place,
            teacherName = teacherName?.full() ?: "",
            beginTimeMins = timeRange.start.time.toMinutes(),
            endTimeMins = timeRange.end.time.toMinutes(),
            note = note ?: ""
        )
    }

    companion object {
        private const val TAG = "LocalScheduleDataSource"
    }
}
