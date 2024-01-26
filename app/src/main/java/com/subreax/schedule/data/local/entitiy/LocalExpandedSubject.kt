package com.subreax.schedule.data.local.entitiy

data class LocalExpandedSubject(
    val id: Long,
    val typeId: String,
    val name: String,
    val place: String,
    val teacher: String,
    val beginTimeMins: Int,
    val endTimeMins: Int,
    val rawGroups: String
)