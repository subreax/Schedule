package com.subreax.schedule.data.model

import java.util.Date

data class LocalScheduleId(
    val localId: Int,
    val remoteId: String,
    val type: ScheduleType,
    val syncTime: Date
)

fun LocalScheduleId.asExternalModel(): ScheduleId {
    return ScheduleId(remoteId, type)
}
