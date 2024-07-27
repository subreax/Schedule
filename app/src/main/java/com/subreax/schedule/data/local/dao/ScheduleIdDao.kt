package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.ScheduleIdEntity
import com.subreax.schedule.data.model.ScheduleType
import java.util.Date

@Dao
interface ScheduleIdDao {
    @Query("SELECT * FROM schedule_id WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: String): ScheduleIdEntity?

    @Query("INSERT INTO schedule_id (remoteId, type, syncTime) VALUES (:remoteId, :type, 0)")
    suspend fun insert(remoteId: String, type: ScheduleType)

    @Query("UPDATE schedule_id SET syncTime = :time WHERE remoteId = :remoteId")
    suspend fun updateSyncTime(remoteId: String, time: Date)
}