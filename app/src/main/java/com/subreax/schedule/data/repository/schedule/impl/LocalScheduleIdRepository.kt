package com.subreax.schedule.data.repository.schedule.impl

import com.subreax.schedule.data.local.dao.ScheduleIdDao
import com.subreax.schedule.data.local.entitiy.ScheduleIdEntity
import com.subreax.schedule.data.model.LocalScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.utils.Resource
import java.util.Date

class LocalScheduleIdRepository(
    private val scheduleIdRepository: ScheduleIdRepository,
    private val scheduleIdDao: ScheduleIdDao
) {
    suspend fun getLocalScheduleId(remoteId: String): Resource<LocalScheduleId> {
        val entity = scheduleIdDao.getByRemoteId(remoteId)
        if (entity != null && entity.typeOrd != ScheduleType.Unknown.ordinal) {
            return Resource.Success(entity.asLocalModel())
        }

        val res = scheduleIdRepository.getScheduleId(remoteId)
        if (res is Resource.Failure) {
            return Resource.Failure(res.message)
        }

        val scheduleId = res.requireValue()
        try {
            scheduleIdDao.insert(
                ScheduleIdEntity(
                    0,
                    scheduleId.value,
                    scheduleId.type.ordinal,
                    0L
                )
            )
        } catch (ignored: Exception) {
        }

        val entity1 = scheduleIdDao.getByRemoteId(remoteId)
        return Resource.Success(entity1!!.asLocalModel())
    }

    suspend fun updateSyncTime(remoteId: String, syncTime: Date) {
        scheduleIdDao.updateSyncTime(remoteId, syncTime.time)
    }

    private fun ScheduleIdEntity.asLocalModel(): LocalScheduleId {
        return LocalScheduleId(
            localId = localId,
            remoteId = remoteId,
            type = ScheduleType.entries[typeOrd],
            syncTime = Date(syncTime)
        )
    }
}