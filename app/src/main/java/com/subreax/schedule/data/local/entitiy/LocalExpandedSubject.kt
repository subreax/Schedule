package com.subreax.schedule.data.local.entitiy

data class LocalExpandedSubject(
    val id: Int,
    val typeId: String,
    val name: String,
    val place: String,
    val teacher: String,
    val beginTimeMins: Long,
    val endTimeMins: Long
)