package com.subreax.schedule.data.local.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "owner", indices = [
        Index("networkId", unique = true)
    ]
)
data class LocalOwner(
    @PrimaryKey(autoGenerate = true)
    val localId: Int,
    val networkId: String,
    /** 0 = student, 1 = teacher, 2 = room */
    @ColumnInfo(name = "type")
    val typeValue: Int,
    val name: String = ""
)
