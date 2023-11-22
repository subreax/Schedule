package com.subreax.schedule.data.local.entitiy

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "owner", indices = [
    Index("name", unique = true)
])
data class LocalOwner(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val type: String
)