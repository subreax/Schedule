package com.subreax.schedule.utils

import android.content.Context
import com.subreax.schedule.R
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

object DateFormatter {
    private val currentYear = Calendar.getInstance().getYear()

    fun format(context: Context, date: Date): String {
        val calendar = GregorianCalendar()
        calendar.time = date
        val calendarNow = GregorianCalendar()

        if (calendar.dateEquals(calendarNow)) {
            return context.getString(
                R.string.today_d_d,
                calendar.getDayOfMonth(), calendar.getMonth()
            )
        }

        calendarNow.add(Calendar.DAY_OF_MONTH, -1)
        if (calendar.dateEquals(calendarNow)) {
            return context.getString(
                R.string.yesterday_d_d,
                calendar.getDayOfMonth(), calendar.getMonth()
            )
        }

        calendarNow.add(Calendar.DAY_OF_MONTH, 2)
        if (calendar.dateEquals(calendarNow)) {
            return context.getString(
                R.string.tomorrow_d_d,
                calendar.getDayOfMonth(), calendar.getMonth()
            )
        }

        val dayOfWeekStr = calendar.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.LONG, Locale.getDefault()
        )!!.uppercaseFirstLetter()

        return if (calendar.getYear() == currentYear) {
            context.getString(
                R.string.date_s_d_d,
                dayOfWeekStr,
                calendar.getDayOfMonth(),
                calendar.getMonth()
            )
        } else {
            context.getString(
                R.string.date_s_d_d_d,
                dayOfWeekStr,
                calendar.getDayOfMonth(),
                calendar.getMonth(),
                calendar.getYear()
            )
        }
    }

    private fun Calendar.dateEquals(other: Calendar): Boolean {
        return getDayOfMonth() == other.getDayOfMonth() &&
                getMonth() == other.getMonth() &&
                getYear() == other.getYear()
    }

    private fun Calendar.getDayOfMonth(): Int = get(Calendar.DAY_OF_MONTH)
    private fun Calendar.getMonth(): Int = get(Calendar.MONTH) + 1
    private fun Calendar.getYear(): Int = get(Calendar.YEAR)

    private fun String.uppercaseFirstLetter(): String {
        if (isNotEmpty()) {
            val first = get(0).uppercaseChar()
            return first + substring(1)
        }
        return this
    }
}