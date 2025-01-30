package com.subreax.schedule.data.model

import java.util.Date

data class Schedule(
    val id: ScheduleId,
    val subjects: List<Subject>,
    val syncTime: Date
)