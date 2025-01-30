package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.ScheduleInfoEntity
import com.subreax.schedule.data.model.ScheduleType
import java.util.Date

@Dao
interface ScheduleInfoDao {
    @Query("SELECT * FROM schedule_info WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: String): ScheduleInfoEntity?

    @Query("INSERT INTO schedule_info (remoteId, type, syncTime) VALUES (:remoteId, :type, 0)")
    suspend fun add(remoteId: String, type: ScheduleType)

    @Query("UPDATE schedule_info SET syncTime = :time WHERE remoteId = :remoteId")
    suspend fun setSyncTime(remoteId: String, time: Date)

    @Query("UPDATE schedule_info SET type = :scheduleType WHERE remoteId = :remoteId")
    suspend fun setType(remoteId: String, scheduleType: ScheduleType)

    @Query("SELECT * FROM schedule_info")
    suspend fun getInfos(): List<ScheduleInfoEntity>

    @Query("DELETE FROM schedule_info WHERE localId = :localId")
    suspend fun deleteByLocalId(localId: Int)

    @Query("DELETE FROM schedule_info WHERE remoteId = :remoteId")
    suspend fun deleteByRemoteId(remoteId: String)
}