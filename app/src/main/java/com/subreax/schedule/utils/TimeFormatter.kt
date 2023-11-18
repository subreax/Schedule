package com.subreax.schedule.utils

import java.util.Calendar
import java.util.Date

object TimeFormatter {
    private fun format2d(i: Int): String {
        if (i >= 10) {
            return "$i"
        }
        return "0$i"
    }

    fun format(calendar: Calendar, time: Date): String {
        calendar.time = time
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        return "${format2d(hours)}:${format2d(minutes)}"
    }
}