package com.subreax.schedule.data.model

data class Subject(
    val id: Long,
    val name: String,
    val nameAlias: String,
    val type: SubjectType,
    val place: String,
    val timeRange: TimeRange,
    val groups: List<Group>,
    val teacher: PersonName?
)
