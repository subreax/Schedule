package com.subreax.schedule.data.model

import com.subreax.schedule.utils.TimeFormatter
import java.util.Calendar
import java.util.Date

class TimeRange(startDate: Date, endDate: Date) {
    val start: Date
    val end: Date

    init {
        if (startDate <= endDate) {
            start = startDate
            end = endDate
        }
        else {
            end = startDate
            start = endDate
        }
    }

    fun contains(time: Date): Boolean {
        return time.time >= start.time && time.time <= end.time
    }

    fun toString(calendar: Calendar): String {
        val startStr = TimeFormatter.format(calendar, start)
        val endStr = TimeFormatter.format(calendar, end)
        return "$startStr - $endStr"
    }
}