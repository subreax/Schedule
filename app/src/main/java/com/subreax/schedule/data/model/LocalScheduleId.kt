package com.subreax.schedule.data.model

import java.util.Date

data class LocalScheduleId(
    val localId: Int,
    val remoteId: String,
    val type: ScheduleType,
    val syncTime: Date
)