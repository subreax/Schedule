package com.subreax.schedule.data.repository.schedule.impl

import com.subreax.schedule.data.local.dao.ScheduleInfoDao
import com.subreax.schedule.data.local.entitiy.ScheduleInfoEntity
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.data.repository.subject.SubjectRepository
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Date

class ScheduleRepositoryImpl(
    private val subjectRepository: SubjectRepository,
    private val scheduleIdRepository: ScheduleIdRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
    private val scheduleInfoDao: ScheduleInfoDao,
    private val externalScope: CoroutineScope
) : ScheduleRepository {
    init {
        externalScope.launch {
            deleteNotBookmarkedSchedules()
        }
    }

    override suspend fun sync(id: String): Resource<Unit> {
        return externalScope.async {
            val now = Date()
            val networkScheduleRes = scheduleNetworkDataSource.getSchedule(id, now)
            if (networkScheduleRes is Resource.Failure) {
                return@async Resource.Failure(networkScheduleRes.message)
            }

            val scheduleInfoRes = getScheduleInfo(id)
            if (scheduleInfoRes is Resource.Failure) {
                return@async Resource.Failure(scheduleInfoRes.message)
            }

            ensureActive()
            val networkSchedule = networkScheduleRes.requireValue()
            val scheduleInfo = scheduleInfoRes.requireValue()
            subjectRepository.replaceSubjects(scheduleInfo.localId, networkSchedule.subjects, now)
            scheduleInfoDao.setSyncTime(id, now)
            Resource.Success(Unit)
        }.await()
    }

    override suspend fun get(id: String): Resource<Schedule> {
        return externalScope.async {
            getScheduleInfo(id).ifSuccess {
                val subjects = subjectRepository.getSubjects(it.localId)
                val schedule = Schedule(it.toScheduleId(), subjects, it.syncTime)
                Resource.Success(schedule)
            }
        }.await()
    }

    override suspend fun getSyncTime(id: String): Date {
        val infoRes = getScheduleInfo(id)
        if (infoRes is Resource.Failure) {
            return Date(0)
        }
        return infoRes.requireValue().syncTime
    }

    override suspend fun clear(id: String): Resource<Unit> {
        return externalScope.async {
            getScheduleInfo(id).ifSuccess {
                subjectRepository.clearSubjects(it.localId, 0)
                scheduleInfoDao.deleteByLocalId(it.localId)
                Resource.Success(Unit)
            }
        }.await()
    }

    private suspend fun getScheduleInfo(remoteId: String): Resource<ScheduleInfoEntity> {
        val entity = scheduleInfoDao.getByRemoteId(remoteId)
        if (entity != null && entity.type != ScheduleType.Unknown) {
            return Resource.Success(entity)
        }

        val scheduleIdRes = scheduleIdRepository.getScheduleId(remoteId)
        if (scheduleIdRes is Resource.Failure) {
            return Resource.Failure(scheduleIdRes.message)
        }

        val scheduleId = scheduleIdRes.requireValue()
        if (entity == null) {
            scheduleInfoDao.add(scheduleId.value, scheduleId.type)
        } else if (entity.type != scheduleId.type) {
            scheduleInfoDao.setType(remoteId, scheduleId.type)
        }
        val entity1 = scheduleInfoDao.getByRemoteId(remoteId)
        return Resource.Success(entity1!!)
    }

    private suspend fun deleteNotBookmarkedSchedules() {
        val bookmarks = withTimeout(5000L) {
            bookmarkRepository.bookmarks.first { it.isNotEmpty() }
        }.map { it.scheduleId.value }

        val infos = scheduleInfoDao.getInfos()
        infos.forEach {
            if (!bookmarks.contains(it.remoteId)) {
                val info = scheduleInfoDao.getByRemoteId(it.remoteId) ?: return
                subjectRepository.clearSubjects(info.localId, 0)
                scheduleInfoDao.deleteByLocalId(info.localId)
            }
        }
    }

    private fun ScheduleInfoEntity.toScheduleId(): ScheduleId {
        return ScheduleId(
            value = remoteId,
            type = type
        )
    }
}