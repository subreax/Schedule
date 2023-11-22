package com.subreax.schedule.data.local.entitiy

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "subject_name", indices = [
    Index("value", unique = true)
])
data class LocalSubjectName(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val value: String,
    val alias: String
)