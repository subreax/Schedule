package com.subreax.schedule.data.repository.schedule.impl

import com.subreax.schedule.data.local.dao.ScheduleIdDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.entitiy.SubjectEntity
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import com.subreax.schedule.data.model.LocalScheduleId
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.data.network.model.NetworkSchedule
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.min2ms
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
                subjectDao.deleteSubjects(it.localId)
                Resource.Success(Unit)
            }
    }

    private suspend fun syncSchedule(scheduleId: LocalScheduleId): Resource<Unit> {
        val minEndTime = scheduleId.syncTime
        val networkScheduleRes = scheduleNetworkDataSource.getSchedule(
            scheduleId.remoteId,
            minEndTime
        )

        return networkScheduleRes.ifSuccess { networkSchedule ->
            saveSchedule(scheduleId, networkSchedule, minEndTime)
            localScheduleIdRepository.updateSyncTime(networkSchedule.id, Date())
            Resource.Success(Unit)
        }
    }

    private suspend fun loadSchedule(scheduleId: LocalScheduleId): Schedule {
        val localSubjects = subjectDao.getSubjects(scheduleId.localId)
        return Schedule(
            id = ScheduleId(
                scheduleId.remoteId,
                scheduleId.type
            ),
            subjects = localSubjects.map { it.asExternalModel() },
            syncTime = scheduleId.syncTime
        )
    }

    private suspend fun saveSchedule(localScheduleId: LocalScheduleId, networkSchedule: NetworkSchedule, minEndTime: Date) {
        subjectDao.deleteSubjectsAfterSpecifiedTime(localScheduleId.localId, minEndTime.time.ms2min())

        val subjects = networkSchedule.subjects.map { it.asEntity(localScheduleId.localId) }
        subjectDao.insert(subjects)
    }

    private suspend fun NetworkSubject.asEntity(ownerId: Int): SubjectEntity {
        return SubjectEntity(
            id = 0L,
            typeId = SubjectType.fromId(type).id,
            ownerId = ownerId,
            subjectNameId = subjectNameLocalDataSource.getEntryByName(name).id,
            place = place,
            teacherNameId = teacherNameLocalDataSource.getEntryByName(teacher ?: "").id,
            beginTimeMins = beginTime.time.ms2min(),
            endTimeMins = endTime.time.ms2min(),
            rawGroups = SubjectEntity.buildRawGroups(groups)
        )
    }

    private suspend fun SubjectEntity.asExternalModel(): Subject {
        val subjectNameEntry = subjectNameLocalDataSource.getEntryById(subjectNameId).requireValue()
        val teacherNameEntry = teacherNameLocalDataSource.getEntryById(teacherNameId).requireValue()
        val teacherName = if (teacherNameEntry.value.isNotEmpty())
            PersonName.parse(teacherNameEntry.value)
        else
            null

        return Subject(
            id = id,
            name = subjectNameEntry.value,
            nameAlias = subjectNameEntry.alias,
            type = SubjectType.fromId(typeId),
            place = place,
            timeRange = TimeRange(
                Date(beginTimeMins.min2ms()),
                Date(endTimeMins.min2ms())
            ),
            groups = SubjectEntity.parseGroups(rawGroups),
            teacher = teacherName
        )
    }

    private fun LocalScheduleId.isScheduleAlive(): Boolean {
        return System.currentTimeMillis() < syncTime.time + SCHEDULE_LIFE_DURATION
    }

    private fun LocalScheduleId.isScheduleExpired(): Boolean {
        return System.currentTimeMillis() >= syncTime.time + SCHEDULE_LIFE_DURATION
    }

    companion object {
        private const val SCHEDULE_LIFE_DURATION = 30L * 60L * 1000L
    }
}