package com.subreax.schedule.data.local.entitiy

import androidx.room.Entity

@Entity(tableName = "subject", primaryKeys = ["owner", "beginTime"])
data class LocalSubject(
    val owner: String,
    val beginTime: Long,
    val endTime: Long,
    val name: String,
    val type: Int,
    val place: String,
    val teacherFirstName: String,
    val teacherLastName: String,
    val teacherMiddleName: String
)
