package com.subreax.schedule.utils

import android.content.res.Resources
import com.subreax.schedule.R
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

    fun formatRelative(resources: Resources, time: Date, now: Date): String {
        val secondsAgo = (now.time - time.time) / 1000L
        if (secondsAgo < 0) {
            throw IllegalArgumentException("'Now' should be greater than 'time'")
        }

        val minutesAgo = (secondsAgo / 60).toInt()
        val hoursAgo = minutesAgo / 60
        val daysAgo = hoursAgo / 24

        return if (minutesAgo == 0) {
            if (secondsAgo <= 10) {
                resources.getString(R.string.just_now)
            } else {
                resources.getQuantityString(R.plurals.seconds_ago, secondsAgo.toInt(), secondsAgo.toInt())
            }
        } else if (hoursAgo == 0) {
            resources.getQuantityString(R.plurals.minutes_ago, minutesAgo, minutesAgo)
        } else if (daysAgo == 0) {
            resources.getQuantityString(R.plurals.hours_ago, hoursAgo, hoursAgo)
        } else {
            resources.getQuantityString(R.plurals.days_ago, daysAgo, daysAgo)
        }
    }
}