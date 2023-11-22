package com.subreax.schedule.data.network.impl

import android.util.Log
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.model.RetrofitSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    private val service: RetrofitService
) : NetworkDataSource {
    override suspend fun getGroupSchedule(group: String): List<NetworkSubject> {
        return withContext(Dispatchers.IO) {
            service.getSchedule(group, "GROUP_P")
                .map(::toNetworkScheduleItem)
        }
    }

    private fun toNetworkScheduleItem(it: RetrofitSubject): NetworkSubject {
        val calendar = Calendar.getInstance()
        val date = it.DATE_Z.parseDate(calendar)
        val (beginTime, endTime) = it.TIME_Z.parseTimeRange(date)
        val teacher = PersonName.parse(it.PREP ?: "")

        return NetworkSubject(
            name = it.transformSubjectName(),
            place = it.AUD,
            beginTime = beginTime,
            endTime = endTime,
            teacher = teacher,
            type = it.CLASS
        )
    }

    // parses date in format dd.mm.yyyy
    private fun String.parseDate(
        calendar: Calendar = GregorianCalendar()
    ): Date {
        val (day, month, year) = split('.')
        calendar.clear()
        calendar.set(
            year.toInt(),
            month.toInt() - 1,
            day.toInt()
        )
        return calendar.time
    }

    // parses time in format 'hh:mm' relative to the date
    private fun String.parseTime(date: Date): Date {
        val (hour, min) = split(':').map { it.toInt() }
        val ms = (hour * 60L + min) * 60L * 1000L
        return Date(date.time + ms)
    }

    // parses time range in format 'hh:mm - hh:mm' relative to the date
    private fun String.parseTimeRange(date: Date): Array<Date> {
        val (beginStr, endStr) = split('-')
            .map { it.trim() }

        val beginTime = beginStr.parseTime(date)
        val endTime = endStr.parseTime(date)
        return arrayOf(beginTime, endTime)
    }

    private fun RetrofitSubject.transformSubjectName(): String {
        return try {
            if (DISCIP == "Иностранный язык") {
                transformSubjectNameAsForeignLang()
            } else if (DISCIP.startsWith("Введение в матема")) {
                "Математический анал"
            } else if (PREP?.startsWith("Юрков") == true) {
                "Душный дед"
            } else if (DISCIP.startsWith("Физическая") && CLASS == "lecture") {
                "Спортивное сидение на лавках"
            } else {
                DISCIP
            }
        } catch (th: Throwable) {
            Log.e("NetworkDataSourceImpl", "Error occurred while parsing specific subject name", th)
            DISCIP
        }
    }

    private fun RetrofitSubject.transformSubjectNameAsForeignLang(): String {
        val ob = KOW.lastIndexOf('(')
        return if (ob != -1) {
            val cb = KOW.length - 1
            val lang = KOW
                .substring(ob + 1, cb)
                .replaceFirstChar { it.uppercaseChar() }

            "$lang язык"
        } else {
            DISCIP
        }
    }
}