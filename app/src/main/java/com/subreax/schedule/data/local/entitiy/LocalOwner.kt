package com.subreax.schedule.data.local.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.subreax.schedule.data.model.ScheduleOwner

@Entity(
    tableName = "owner", indices = [
        Index("networkId", unique = true)
    ]
)
data class LocalOwner(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val networkId: String,
    /**
     * 0 = student
     * 1 = teacher
     * 2 = room
     * */
    @ColumnInfo(name = "type")
    val typeValue: Int,
    val name: String = ""
)

fun ScheduleOwner.toLocal(localId: Int = 0): LocalOwner {
    return LocalOwner(localId, this.id, this.type.ordinal, this.name)
}

fun LocalOwner.toModel(): ScheduleOwner {
    val type = ScheduleOwner.Type.entries[this.typeValue]
    return ScheduleOwner(this.networkId, type, name)
}