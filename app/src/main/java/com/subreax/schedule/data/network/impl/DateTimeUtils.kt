package com.subreax.schedule.data.network.impl

import android.icu.util.Calendar
import android.icu.util.TimeZone
import java.util.Date
import kotlin.time.Duration.Companion.minutes

object DateTimeUtils {
    private const val MINUTE_MS = 60000L
    private const val HOUR_MS = 3600000L

    /** Parses time range from tulsu.ru
     * @param dateStr format: 'DD.MM.YYYY'
     * @param timeRangeStr format: 'HH:MM - HH:MM' (GMT+3)
     * */
    fun parseTimeRange(dateStr: String, timeRangeStr: String): Array<Date> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeZone = TimeZone.GMT_ZONE
        3L.minutes.inWholeMilliseconds

        val date = parseDate(dateStr, calendar)
        return parseTimeRange(timeRangeStr, date, 3 * HOUR_MS)
    }


    // parses date in format 'DD.MM.YYYY'
    private fun parseDate(str: String, calendar: Calendar): Date {
        val (day, month, year) = str.split('.')
        calendar.set(
            year.toInt(),
            month.toInt() - 1,
            day.toInt()
        )
        return calendar.time
    }

    // parses time range in format 'HH:MM - HH:MM' relative to the date
    private fun parseTimeRange(str: String, date: Date, timezoneOffset: Long): Array<Date> {
        val (beginStr, endStr) = str.split('-')
            .map { it.trim() }

        val beginTime = beginStr.parseTime(date, timezoneOffset)
        val endTime = endStr.parseTime(date, timezoneOffset)
        return arrayOf(beginTime, endTime)
    }

    // parses time in format 'hh:mm' relative to the date
    private fun String.parseTime(date: Date, timezoneOffset: Long): Date {
        val (hour, min) = split(':').map { it.toInt() }
        val ms = (hour * 60L + min) * MINUTE_MS
        return Date(date.time + ms - timezoneOffset)
    }
}
