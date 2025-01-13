package com.subreax.schedule.data.network.schedule.tsu

import android.icu.util.Calendar
import android.icu.util.TimeZone
import com.subreax.schedule.data.model.TimeRange
import java.util.Date

object DateTimeUtils {
    private const val MINUTE_MS = 60000L
    private const val HOUR_MS = 3600000L

    /** Parses time range from tulsu.ru
     * @param dateStr format: 'DD.MM.YYYY'
     * @param timeRangeStr format: 'HH:MM - HH:MM' (GMT+3)
     * */
    fun parseTimeRange(dateStr: String, timeRangeStr: String, calendar: Calendar = Calendar.getInstance()): TimeRange {
        calendar.timeZone = TimeZone.GMT_ZONE

        val date = parseDate(dateStr, calendar)
        return parseTimeRange(timeRangeStr, date, 3 * HOUR_MS)
    }


    // parses date in format 'DD.MM.YYYY' or 'DD.MM.YY'
    fun parseDate(str: String, calendar: Calendar = Calendar.getInstance()): Date {
        val (day, month, year0) = str.split('.')
        var year = year0.toInt()
        if (year < 1000) {
            year += 2000
        }

        calendar.set(
            year,
            month.toInt() - 1,
            day.toInt(),
            0,
            0,
            0
        )
        return calendar.time
    }

    // parses time range in format 'HH:MM - HH:MM' relative to the date
    private fun parseTimeRange(str: String, date: Date, timezoneOffset: Long): TimeRange {
        val (beginStr, endStr) = str.split('-')
            .map { it.trim() }

        val beginTime = beginStr.parseTime(date, timezoneOffset)
        val endTime = endStr.parseTime(date, timezoneOffset)
        return TimeRange(beginTime, endTime)
    }

    // parses time in format 'hh:mm' relative to the date
    private fun String.parseTime(date: Date, timezoneOffset: Long): Date {
        val (hour, min) = split(':').map { it.toInt() }
        val ms = (hour * 60L + min) * MINUTE_MS
        return Date(date.time + ms - timezoneOffset)
    }
}
