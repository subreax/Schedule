package com.subreax.schedule.data.local.entitiy

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.subreax.schedule.data.model.ScheduleType
import java.util.Date

@Entity(tableName = "schedule_id", indices = [
    Index("localId", unique = true)
])
data class ScheduleIdEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int,
    val remoteId: String,
    val type: ScheduleType,
    val syncTime: Date
)
