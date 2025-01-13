package com.subreax.schedule.utils

import java.util.Date

object DateTimeUtils {
    const val ONE_DAY_MS = 1000 * 60 * 60 * 24L

    fun getDaysBetweenInclusive(from: Date, to: Date): Int {
        return ((to.time - from.time + ONE_DAY_MS) / ONE_DAY_MS).toInt()
    }

    fun keepDateAndRemoveTime(dateTime: Long): Long {
        return dateTime - dateTime % ONE_DAY_MS
    }
}
