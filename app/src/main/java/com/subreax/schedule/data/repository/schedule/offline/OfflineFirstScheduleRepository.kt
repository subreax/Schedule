package com.subreax.schedule.data.repository.schedule.offline

import com.subreax.schedule.R
import com.subreax.schedule.data.local.dao.ScheduleInfoDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.entitiy.ScheduleInfoEntity
import com.subreax.schedule.data.local.entitiy.SubjectEntity
import com.subreax.schedule.data.local.entitiy.asExternalModel
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import com.subreax.schedule.data.model.AcademicScheduleItem
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.network.model.NetworkSchedule
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.di.DefaultDispatcher
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.ms2min
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Date
import javax.inject.Inject

class OfflineFirstScheduleRepository @Inject constructor(
    private val scheduleIdRepository: ScheduleIdRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
    private val subjectNameLocalDataSource: SubjectNameLocalDataSource,
    private val teacherNameLocalDataSource: TeacherNameLocalDataSource,
    private val subjectDao: SubjectDao,
    private val scheduleInfoDao: ScheduleInfoDao,
    private val externalScope: CoroutineScope,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ScheduleRepository {
    init {
        externalScope.launch {
            deleteNotBookmarkedSchedules()
        }
    }

    override suspend fun getSchedule(id: String, invalidate: Boolean): Resource<Schedule> {
        return externalScope.async {
            val scheduleInfoRes = getScheduleInfo(id)
            if (scheduleInfoRes is Resource.Failure) {
                return@async Resource.Failure(scheduleInfoRes.message)
            }

            val scheduleInfo = scheduleInfoRes.requireValue()
            if (!invalidate && isScheduleAlive(scheduleInfo.syncTime)) {
                Resource.Success(loadSchedule(id))
            } else {
                val syncRes = syncSchedule(scheduleInfo)
                if (syncRes is Resource.Failure) {
                    Resource.Failure(syncRes.message, loadSchedule(id))
                } else {
                    Resource.Success(loadSchedule(id))
                }
            }
        }.await()
    }

    override suspend fun getAcademicSchedule(id: String): Resource<List<AcademicScheduleItem>> {
        return withContext(defaultDispatcher) { scheduleNetworkDataSource.getAcademicSchedule(id) }
    }

    override suspend fun getSubjectById(id: Long): Resource<Subject> {
        return withContext(defaultDispatcher) {
            val subject = subjectDao.findSubjectById(id)
            if (subject != null) {
                Resource.Success(subject.asExternalModel())
            } else {
                Resource.Failure(UiText.hardcoded("Subject with id '$id' not found"))
            }
        }
    }

    override suspend fun setSubjectNameAlias(
        subjectName: String,
        nameAlias: String
    ): Resource<Unit> {
        return externalScope.async {
            subjectNameLocalDataSource.setNameAlias(subjectName, nameAlias.trim())
            Resource.Success(Unit)
        }.await()
    }

    override suspend fun clearCache(id: String): Resource<Unit> {
        return externalScope.async {
            getScheduleInfo(id).ifSuccess {
                subjectDao.deleteSubjects(it.localId, 0)
                Resource.Success(Unit)
            }
        }.await()
    }

    private suspend fun deleteNotBookmarkedSchedules() {
        val bookmarks = withTimeout(5000L) {
            bookmarkRepository.bookmarks.first { it.isNotEmpty() }
        }.map { it.scheduleId.value }

        val infos = scheduleInfoDao.getInfos()
        infos.forEach {
            if (!bookmarks.contains(it.remoteId)) {
                val info = scheduleInfoDao.getByRemoteId(it.remoteId) ?: return
                subjectDao.deleteSubjects(info.localId, 0)
                scheduleInfoDao.deleteByLocalId(info.localId)
            }
        }
    }

    private suspend fun syncSchedule(scheduleInfo: ScheduleInfoEntity): Resource<Unit> {
        val syncTime = scheduleInfo.syncTime
        val networkScheduleRes = withTimeoutOrNull(SYNC_TIMEOUT) {
            scheduleNetworkDataSource.getSchedule(
                scheduleInfo.remoteId,
                syncTime
            )
        } ?: Resource.Failure(UiText.res(R.string.failed_to_update_schedule))

        return networkScheduleRes.ifSuccess { networkSchedule ->
            if (networkSchedule.subjects.isNotEmpty()) {
                // объяснение +1
                // пусть обновление происходит в 13:09. тогда удалится текущий предмет, который кончается в 13:10 и позже
                // пусть обновление происходит в 13:10. тогда предмет, который кончается в эту минуту, останется. а все предметы позже удалятся
                subjectDao.deleteSubjects(scheduleInfo.localId, syncTime.time.ms2min() + 1)
                saveSchedule(scheduleInfo, networkSchedule)
                scheduleInfoDao.setSyncTime(networkSchedule.id, Date())
            } else {
                scheduleInfoDao.setSyncTime(networkSchedule.id, Date())
            }
            Resource.Success(Unit)
        }
    }

    private suspend fun loadSchedule(id: String): Schedule {
        val scheduleInfo = getScheduleInfo(id).requireValue()
        val localSubjects = subjectDao.getSubjects(scheduleInfo.localId)
        return Schedule(
            id = scheduleInfo.toScheduleId(),
            subjects = localSubjects.map { it.asExternalModel() },
            syncTime = scheduleInfo.syncTime,
            expiresAt = Date(scheduleInfo.syncTime.time + SCHEDULE_LIFE_DURATION)
        )
    }

    private suspend fun saveSchedule(
        scheduleInfo: ScheduleInfoEntity,
        networkSchedule: NetworkSchedule
    ) {
        val subjects = networkSchedule.subjects
            .map { it.asEntity(scheduleInfo.localId) }

        subjectDao.insertSubjects(subjects)
    }

    private suspend fun getScheduleInfo(remoteId: String): Resource<ScheduleInfoEntity> {
        val entity = scheduleInfoDao.getByRemoteId(remoteId)
        if (entity != null && entity.type != ScheduleType.Unknown) {
            return Resource.Success(entity)
        }

        val res = scheduleIdRepository.getScheduleId(remoteId)
        if (res is Resource.Failure) {
            return Resource.Failure(res.message)
        }

        val scheduleId = res.requireValue()
        if (entity == null) {
            scheduleInfoDao.add(scheduleId.value, scheduleId.type)
        } else if (entity.type != scheduleId.type) {
            scheduleInfoDao.setType(remoteId, scheduleId.type)
        }
        val entity1 = scheduleInfoDao.getByRemoteId(remoteId)
        return Resource.Success(entity1!!)
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

    private fun ScheduleInfoEntity.toScheduleId(): ScheduleId {
        return ScheduleId(
            value = remoteId,
            type = type
        )
    }

    companion object {
        private const val TAG = "ScheduleRepositoryImpl"
        private const val SCHEDULE_LIFE_DURATION = 30L * 60L * 1000L
        private const val SYNC_TIMEOUT = 10000L

        private fun isScheduleAlive(syncTime: Date): Boolean {
            return System.currentTimeMillis() < syncTime.time + SCHEDULE_LIFE_DURATION
        }

        private fun isScheduleExpired(syncTime: Date) = !isScheduleAlive(syncTime)
    }
}