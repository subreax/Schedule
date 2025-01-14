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

    /** Пример результата:
     * - `05:23`
     * - `00:08`
     * */
    fun formatHhMm(calendar: Calendar, time: Date): String {
        calendar.time = time
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        return "${format2d(hours)}:${format2d(minutes)}"
    }

    /** Я без понятия как назвать эту функцию.
     * Пример результата:
     * - `5h 23m`
     * - `8m`
     * */
    fun formatHhMm2(res: Resources, time: Long): String {
        val minutes = time / 60000L
        return if (minutes <= 60) {
            res.getString(R.string.d_m, minutes)
        } else {
            res.getString(R.string.d_h_d_m, minutes / 60, minutes % 60)
        }
    }

    fun formatRelative(resources: Resources, time: Date, now: Date): String {
        return formatRelative(resources, now.time - time.time)
    }

    fun formatRelative(resources: Resources, diffMs: Long): String {
        if (diffMs < 0) {
            return resources.getString(R.string.in_the_future)
        }

        val secondsAgo = (diffMs / 60000).toInt()
        val minutesAgo = secondsAgo / 60

        return if (minutesAgo == 0 && secondsAgo <= 10) {
            resources.getString(R.string.just_now)
        } else {
            resources.getString(R.string.s_ago, formatDurationApprox(resources, diffMs))
        }
    }

    fun formatDurationApprox(resources: Resources, duration: Long): String {
        if (duration < 0) {
            return "wtf $duration"
        }

        val seconds = (duration / 1000).toInt()
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return formatDurationApprox(resources, seconds, minutes, hours, days)
    }

    private fun formatDurationApprox(
        resources: Resources,
        seconds: Int,
        minutes: Int,
        hours: Int,
        days: Int
    ): String {
        return if (minutes == 0) {
            resources.getQuantityString(R.plurals.seconds, seconds, seconds)
        } else if (hours == 0) {
            resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        } else if (days == 0) {
            resources.getQuantityString(R.plurals.hours, hours, hours)
        } else {
            resources.getQuantityString(R.plurals.days, days, days)
        }
    }
}