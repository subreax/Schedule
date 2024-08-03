package com.subreax.schedule.data.local.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.subreax.schedule.data.model.ScheduleType
import java.util.Date

@Entity(tableName = "schedule_info", indices = [
    Index("localId", unique = true)
])
data class ScheduleInfoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("localId")
    val localId: Int,
    @ColumnInfo("remoteId")
    val remoteId: String,
    @ColumnInfo("type")
    val type: ScheduleType,
    @ColumnInfo("syncTime")
    val syncTime: Date
)
