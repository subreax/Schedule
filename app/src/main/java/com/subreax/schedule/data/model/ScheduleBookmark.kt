package com.subreax.schedule.data.model

data class ScheduleBookmark(
    val scheduleId: ScheduleId,
    val name: String = NO_NAME,
    val position: Int = NO_POSITION
) {
    fun hasName() = name != NO_NAME

    fun nameOrId() = if (name != NO_NAME) {
        name
    } else {
        scheduleId.value
    }

    companion object {
        const val NO_NAME = ""
        const val NO_POSITION = -1
    }
}
