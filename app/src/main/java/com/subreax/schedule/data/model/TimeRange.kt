package com.subreax.schedule.data.model

import com.subreax.schedule.utils.TimeFormatter
import java.util.Calendar
import java.util.Date

class TimeRange(startDate: Date, endDate: Date) {
    val begin: Date
    val end: Date

    init {
        if (startDate <= endDate) {
            begin = startDate
            end = endDate
        }
        else {
            end = startDate
            begin = endDate
        }
    }

    fun contains(time: Date): Boolean {
        return time.time >= begin.time && time.time <= end.time
    }

    fun contains(time: Long): Boolean {
        return time >= begin.time && time <= end.time
    }

    fun toString(calendar: Calendar): String {
        val startStr = TimeFormatter.formatHhMm(calendar, begin)
        val endStr = TimeFormatter.formatHhMm(calendar, end)
        return "$startStr - $endStr"
    }
}