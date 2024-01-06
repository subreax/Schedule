package com.subreax.schedule.data.model

data class Subject(
    val id: Int,
    val name: String,
    val place: String,
    val type: SubjectType,
    val timeRange: TimeRange,
    val teacherName: PersonName?,
    val note: String?
)
