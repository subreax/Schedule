package com.subreax.schedule.data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject")
data class LocalSubject(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val type: Int,
    val ownerId: Int, // fk
    val nameId: Int, // fk
    val place: String, // todo: should be fk
    val teacherName: String, // todo: should be fk
    val beginTimeMins: Long,
    val endTimeMins: Long
)
