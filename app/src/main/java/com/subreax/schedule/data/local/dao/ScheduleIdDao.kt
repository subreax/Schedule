package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.ScheduleIdEntity

@Dao
interface ScheduleIdDao {
    @Query("SELECT * FROM schedule_id WHERE remoteId = :remoteId")
    suspend fun getByRemoteId(remoteId: String): ScheduleIdEntity?

    @Insert
    suspend fun insert(entity: ScheduleIdEntity)

    @Query("UPDATE schedule_id SET syncTime = :time WHERE remoteId = :remoteId")
    suspend fun updateSyncTime(remoteId: String, time: Long)
}