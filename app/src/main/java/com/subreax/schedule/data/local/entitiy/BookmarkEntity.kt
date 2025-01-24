package com.subreax.schedule.data.local.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType

@Entity(
    tableName = "bookmarks", indices = [
        Index("scheduleId", unique = true)
    ]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val scheduleId: String,
    val type: ScheduleType,
    val name: String,
    @ColumnInfo(defaultValue = "0")
    val position: Int
)

fun BookmarkEntity.asExternalModel(): ScheduleBookmark {
    return ScheduleBookmark(
        name = name,
        scheduleId = ScheduleId(
            value = scheduleId,
            type = type
        ),
        position = position
    )
}
