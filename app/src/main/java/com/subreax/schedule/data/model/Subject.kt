package com.subreax.schedule.data.model

data class Subject(
    val name: String,
    val place: String,
    val type: SubjectType,
    val timeRange: TimeRange,
    val teacherName: PersonName?
)
