package com.subreax.schedule.data.model

data class ScheduleBookmark(
    val scheduleId: ScheduleId,
    val name: String
) {
    fun hasName() = name != NO_NAME

    companion object {
        const val NO_NAME = ""
    }
}
