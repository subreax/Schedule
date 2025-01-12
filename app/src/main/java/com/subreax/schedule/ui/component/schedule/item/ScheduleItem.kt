package com.subreax.schedule.ui.component.schedule.item

import android.content.Context
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.utils.DateFormatter
import com.subreax.schedule.utils.join
import com.subreax.schedule.utils.toLocalizedString
import java.util.Calendar
import java.util.Date

sealed class ScheduleItem(val begin: Date) {
    class Title(val title: String, begin: Date) : ScheduleItem(begin) {
        companion object {
            const val ContentType = 1
        }
    }

    abstract class TimeRangeItem(begin: Date, val end: Date) : ScheduleItem(begin) {
        init {
            if (begin > end) {
                throw IllegalStateException("begin > end")
            }
        }

        fun getMinutesLeftOrZero(inclusive: Boolean = false): Int {
            val incr = if (inclusive) 1 else 0
            val minutesLeft = ((end.time - now) / 60000 + incr).toInt().coerceAtLeast(0)
            return if (isActive) minutesLeft else 0
        }

        val isActive: Boolean
            get() = now >= begin.time && now <= end.time

        val isExpired: Boolean
            get() = now > end.time

        private val now: Long
            get() = System.currentTimeMillis()
    }

    class Subject(
        val id: Long,
        val index: String,
        begin: Date,
        end: Date,
        val title: String,
        val subtitle: String,
        val type: SubjectType,
        val note: String?
    ) : TimeRangeItem(begin, end) {
        companion object {
            const val ContentType = 2
        }
    }

    class ActiveLabel(start: Date, end: Date) : TimeRangeItem(start, end) {
        companion object {
            const val ContentType = 3
        }
    }

    class PendingLabel(start: Date, end: Date) : TimeRangeItem(start, end) {
        companion object {
            const val ContentType = 4
        }
    }
}

fun List<Subject>.toScheduleItems(
    context: Context,
    scheduleType: ScheduleType
): List<ScheduleItem> {
    return when (scheduleType) {
        ScheduleType.Student,
        ScheduleType.Unknown -> {
            toScheduleItems(
                context = context,
                itemSubtitle = ::buildStudentSubjectItemSubtitle,
                itemNote = {
                    it.groups.first().note
                }
            )
        }

        ScheduleType.Teacher -> {
            toScheduleItems(
                context = context,
                itemSubtitle = ::buildTeacherSubjectItemSubtitle,
                itemNote = { null }
            )
        }

        ScheduleType.Room -> {
            toScheduleItems(
                context = context,
                itemSubtitle = ::buildRoomSubjectItemSubtitle,
                itemNote = { null }
            )
        }
    }
}

private fun List<Subject>.toScheduleItems(
    context: Context,
    itemSubtitle: (Context, Subject) -> String,
    itemNote: (Subject) -> String?,
): List<ScheduleItem> {
    val now = System.currentTimeMillis()
    val calendar = Calendar.getInstance()
    val items = mutableListOf<ScheduleItem>()
    var oldSubjectDay = -1
    this.forEach {
        calendar.time = it.timeRange.start
        val subjectDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (oldSubjectDay != subjectDay) {
            val title = DateFormatter.format(context, it.timeRange.start)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            items.add(ScheduleItem.Title(title, calendar.time))
            oldSubjectDay = subjectDay
        }

        val msBeforeStart = it.timeRange.start.time - now
        if (msBeforeStart > 0 && msBeforeStart < 60000 * 60 * 8) {
            val prevItem = items[items.size - 1]
            val start = if (prevItem is ScheduleItem.TimeRangeItem) {
                prevItem.end
            } else {
                Date(prevItem.begin.time + 1000)
            }

            items.add(ScheduleItem.PendingLabel(start = start, end = it.timeRange.start))
        }

        val msBeforeEnd = it.timeRange.end.time - now
        if (msBeforeEnd > 0 && msBeforeEnd < 60000 * 60 * 8) {
            items.add(ScheduleItem.ActiveLabel(Date(it.timeRange.start.time + 1), it.timeRange.end))
        }

        items.add(
            ScheduleItem.Subject(
                id = it.id,
                index = it.timeRange.getSubjectIndex(calendar),
                begin = it.timeRange.start,
                end = it.timeRange.end,
                title = it.nameAlias.ifEmpty { it.name },
                subtitle = itemSubtitle(context, it),
                type = it.type,
                note = itemNote(it)
            )
        )
    }

    return items
}

private fun buildStudentSubjectItemSubtitle(context: Context, subject: Subject): String {
    val typeName = if (subject.type.ordinal > 2)
        subject.type.toLocalizedString(context)
    else
        ""

    return " • ".join(subject.place, typeName, subject.teacher?.compact() ?: "")
}

private fun buildTeacherSubjectItemSubtitle(context: Context, subject: Subject): String {
    val groups = mapGroupsToStrings(subject.groups)
    val typeName = if (subject.type.ordinal > 2)
        subject.type.toLocalizedString(context)
    else
        ""

    return " • ".join(subject.place, typeName, *groups.toTypedArray())
}

private fun buildRoomSubjectItemSubtitle(context: Context, subject: Subject): String {
    val teacherName = subject.teacher?.compact() ?: ""
    val groups = mapGroupsToStrings(subject.groups)
    val typeName = if (subject.type.ordinal > 2)
        subject.type.toLocalizedString(context)
    else
        ""

    return " • ".join(teacherName, typeName, *groups.toTypedArray())
}

private fun mapGroupsToStrings(groups: List<Group>): List<String> {
    return groups.map {
        if (it.note == null) {
            it.id
        } else {
            "${it.id} (${it.note})"
        }
    }
}

private fun TimeRange.getSubjectIndex(calendar: Calendar): String {
    val mins = ((start.time / 60000L) % (60 * 24)).toInt()
    return when (mins) {
        gmt3MinutesOf(7, 45) -> "1"
        gmt3MinutesOf(9, 40) -> "2"
        gmt3MinutesOf(11, 35) -> "3"
        gmt3MinutesOf(13, 40) -> "4"
        gmt3MinutesOf(15, 35) -> "5"
        gmt3MinutesOf(17, 30) -> "6"
        else -> {
            val zoneOffsetMs = calendar.get(Calendar.ZONE_OFFSET)
            val m = mins % 60
            val h = ((start.time + zoneOffsetMs) / (1000 * 60 * 60L)) % 24

            val mm = if (m >= 10) "$m" else "0$m"
            val hh = if (h >= 10) "$h" else "0$h"
            return "$hh\n$mm"
        }
    }
}

/** Returns total minutes of time since the beginning of the day.
It should be passed in GMT+3 */
private fun gmt3MinutesOf(hours: Int, mins: Int): Int {
    return (hours - 3) * 60 + mins
}
