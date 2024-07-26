package com.subreax.schedule.data.local.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_id")
data class ScheduleIdEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int,
    val remoteId: String,
    @ColumnInfo("type")
    val typeOrd: Int,
    val syncTime: Long
)
