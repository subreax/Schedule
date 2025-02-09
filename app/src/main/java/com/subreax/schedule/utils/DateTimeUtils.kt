package com.subreax.schedule.utils

import java.util.Calendar
import java.util.Date

object DateTimeUtils {
    const val ONE_DAY_MS = 1000 * 60 * 60 * 24L

    private val calendar = Calendar.getInstance()
    val timezoneOffset = calendar.get(Calendar.ZONE_OFFSET)

    fun getDaysBetweenInclusive(from: Date, to: Date): Int {
        return ((to.time - from.time + ONE_DAY_MS) / ONE_DAY_MS).toInt()
    }

    fun keepDateAndRemoveTime(dateTime: Long): Long {
        val t = dateTime + timezoneOffset
        return (t - (t % ONE_DAY_MS)) - timezoneOffset
    }
}
