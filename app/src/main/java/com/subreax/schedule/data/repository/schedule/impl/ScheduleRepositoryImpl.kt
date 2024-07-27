package com.subreax.schedule.data.repository.schedule.impl

import android.util.Log
import com.subreax.schedule.data.local.dao.ScheduleIdDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.entitiy.SubjectEntity
import com.subreax.schedule.data.local.entitiy.asExternalModel
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import com.subreax.schedule.data.model.LocalScheduleId
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.asExternalModel
import com.subreax.schedule.data.network.model.NetworkSchedule
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.ms2min
import java.util.Date
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    scheduleIdRepository: ScheduleIdRepository,
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
    private val subjectNameLocalDataSource: SubjectNameLocalDataSource,
    private val teacherNameLocalDataSource: TeacherNameLocalDataSource,
    scheduleIdDao: ScheduleIdDao,
    private val subjectDao: SubjectDao
) : ScheduleRepository {
    private val localScheduleIdRepository = LocalScheduleIdRepository(scheduleIdRepository, scheduleIdDao)

    override suspend fun getSchedule(id: String): Resource<Schedule> {
        val localScheduleIdRes = localScheduleIdRepository.getLocalScheduleId(id)
        if (localScheduleIdRes is Resource.Failure) {
            return Resource.Failure(localScheduleIdRes.message)
        }

        val localScheduleId = localScheduleIdRes.requireValue()
        var syncErrorMessage: UiText? = null
        if (localScheduleId.isScheduleExpired()) {
            val res = syncSchedule(localScheduleId)
            if (res is Resource.Failure) {
                syncErrorMessage = res.message
            }
        }

        return if (syncErrorMessage == null) {
            Resource.Success(loadSchedule(localScheduleId))
        } else {
            Resource.Failure(syncErrorMessage, loadSchedule(localScheduleId))
        }
    }

    override suspend fun getSubjectById(id: Long): Resource<Subject> {
        val subject = subjectDao.findSubjectById(id)
        return if (subject != null) {
            Resource.Success(subject.asExternalModel())
        } else {
            Resource.Failure(UiText.hardcoded("Subject with id '$id' not found"))
        }
    }

    override suspend fun setSubjectNameAlias(
        subjectName: String,
        nameAlias: String
    ): Resource<Unit> {
        subjectNameLocalDataSource.setNameAlias(subjectName, nameAlias)
        return Resource.Success(Unit)
    }

    override suspend fun clearCache(id: String): Resource<Unit> {
        return localScheduleIdRepository.getLocalScheduleId(id)
            .ifSuccess {
                subjectDao.deleteSubjects(it.localId, 0)
                Resource.Success(Unit)
            }
    }

    private suspend fun syncSchedule(scheduleId: LocalScheduleId): Resource<Unit> {
        val syncFromTime = scheduleId.syncTime
        val networkScheduleRes = scheduleNetworkDataSource.getSchedule(
            scheduleId.remoteId,
            syncFromTime
        )

        return networkScheduleRes.ifSuccess { networkSchedule ->
            subjectDao.deleteSubjects(scheduleId.localId, syncFromTime.time.ms2min())
            saveSchedule(scheduleId, networkSchedule)
            localScheduleIdRepository.updateSyncTime(networkSchedule.id, Date())
            Resource.Success(Unit)
        }
    }

    private suspend fun loadSchedule(id: LocalScheduleId): Schedule {
        val localSubjects = subjectDao.getSubjects(id.localId)
        return Schedule(
            id = id.asExternalModel(),
            subjects = localSubjects.map { it.asExternalModel() },
            syncTime = id.syncTime
        )
    }

    private suspend fun saveSchedule(localScheduleId: LocalScheduleId, networkSchedule: NetworkSchedule) {
        val t0 = System.currentTimeMillis()
        val subjects = networkSchedule.subjects
            .map { it.asEntity(localScheduleId.localId) }
        val t1 = System.currentTimeMillis()
        Log.d(TAG, "Mapping ${subjects.size} subjects: ${t1 - t0} ms")

        subjectDao.insertSubjects(subjects)
    }

    private suspend fun NetworkSubject.asEntity(scheduleId: Int): SubjectEntity {
        return SubjectEntity(
            id = 0L,
            typeId = SubjectType.fromId(type).id,
            scheduleId = scheduleId,
            subjectNameId = subjectNameLocalDataSource.getEntryByName(name).id,
            place = place,
            teacherNameId = teacherNameLocalDataSource.getEntryByName(teacher ?: "").id,
            beginTimeMins = beginTime.time.ms2min(),
            endTimeMins = endTime.time.ms2min(),
            rawGroups = SubjectEntity.buildRawGroups(groups)
        )
    }

    private fun LocalScheduleId.isScheduleAlive(): Boolean {
        return System.currentTimeMillis() < syncTime.time + SCHEDULE_LIFE_DURATION
    }

    private fun LocalScheduleId.isScheduleExpired(): Boolean {
        return System.currentTimeMillis() >= syncTime.time + SCHEDULE_LIFE_DURATION
    }

    companion object {
        private const val TAG = "ScheduleRepositoryImpl"
        private const val SCHEDULE_LIFE_DURATION = 30L * 60L * 1000L
    }
}