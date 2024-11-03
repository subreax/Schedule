package com.subreax.schedule

import com.subreax.schedule.utils.DateTimeUtils
import org.junit.Assert
import org.junit.Test
import java.util.Calendar
import java.util.Date

class DateTimeUtilsTest {
    @Test
    fun inclusive1() {
        val t1 = getTime(2024, Calendar.JANUARY, 10, 15, 0, 0)
        val t2 = getTime(2024, Calendar.JANUARY, 15, 23, 59, 59)
        Assert.assertEquals(6, DateTimeUtils.getDaysBetweenInclusive(t1, t2))
    }

    @Test
    fun inclusive2() {
        val t1 = getTime(2024, Calendar.JANUARY, 10, 23, 59, 59)
        val t2 = getTime(2024, Calendar.JANUARY, 15, 23, 59, 59)
        Assert.assertEquals(6, DateTimeUtils.getDaysBetweenInclusive(t1, t2))
    }

    @Test
    fun inclusive3() {
        val t1 = getTime(2024, Calendar.JANUARY, 11, 0, 0, 59)
        val t2 = getTime(2024, Calendar.JANUARY, 15, 23, 59, 59)
        Assert.assertEquals(5, DateTimeUtils.getDaysBetweenInclusive(t1, t2))
    }

    @Test
    fun inclusive4() {
        val t1 = getTime(2024, Calendar.JANUARY, 15, 10, 0, 0)
        val t2 = getTime(2024, Calendar.JANUARY, 15, 23, 59, 59)
        Assert.assertEquals(1, DateTimeUtils.getDaysBetweenInclusive(t1, t2))
    }

    @Test
    fun inclusive5() {
        val t1 = getTime(2024, Calendar.NOVEMBER, 3, 21, 35, 0)
        val t2 = getTime(2024, Calendar.DECEMBER, 29, 0, 0, 0)
        Assert.assertEquals(56, DateTimeUtils.getDaysBetweenInclusive(t1, t2))
    }

    private fun getTime(year: Int, month: Int, date: Int, hourOfDay: Int, minute: Int, second: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, date, hourOfDay, minute, second)
        return calendar.time
    }
}