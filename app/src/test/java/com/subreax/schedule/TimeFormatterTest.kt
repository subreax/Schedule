package com.subreax.schedule

import com.subreax.schedule.utils.TimeFormatter
import org.junit.Assert
import org.junit.Test
import java.util.Date
import java.util.GregorianCalendar

class TimeFormatterTest {
    @Test
    fun dateWithTwoDigits() {
        val date = Date(1700312107000L)
        val expected = "15:55"

        val calendar = GregorianCalendar()
        Assert.assertEquals(expected, TimeFormatter.format(calendar, date))
    }

    @Test
    fun dateWithOneDigit() {
        val date = Date(1700287505000L)
        val expected = "09:05"

        val calendar = GregorianCalendar()
        Assert.assertEquals(expected, TimeFormatter.format(calendar, date))
    }
}
