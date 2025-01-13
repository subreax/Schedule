package com.subreax.schedule.ui.component.schedule.item

import android.content.Context
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.ui.component.schedule.UiScheduleConstants
import com.subreax.schedule.utils.DateFormatter
import com.subreax.schedule.utils.join
import com.subreax.schedule.utils.toLocalizedString
import java.util.Calendar
import java.util.Date

sealed class ScheduleItem(val key: Long, val timeRange: TimeRange) {
    fun getMinutesLeftOrZero(inclusive: Boolean = false): Int {
        val incr = if (inclusive) 1 else 0
        val minutesLeft = ((timeRange.end.time - now) / 60000 + incr).toInt().coerceAtLeast(0)
        return if (isActive) minutesLeft else 0
    }

    val isActive: Boolean
        get() = timeRange.contains(now)

    val isExpired: Boolean
        get() = now > timeRange.end.time

    private val now: Long
        get() = System.currentTimeMillis()



    class Title(val title: String, date: Date) : ScheduleItem(
        key = date.time,
        timeRange = TimeRange(date, Date(date.time + 1000L))
    ) {
        companion object {
            const val ContentType = 1
        }
    }

    class Subject(
        val id: Long,
        val index: String,
        timeRange: TimeRange,
        val title: String,
        val subtitle: String,
        val type: SubjectType,
        val note: String?
    ) : ScheduleItem(key = timeRange.begin.time, timeRange = timeRange) {
        companion object {
            const val ContentType = 2
        }
    }

    class ActiveLabel(timeRange: TimeRange) : ScheduleItem(
        key = timeRange.begin.time - 1000,
        timeRange = timeRange
    ) {
        companion object {
            const val ContentType = 3
        }
    }

    class PendingLabel(timeRange: TimeRange) : ScheduleItem(
        key = timeRange.begin.time - 2000,
        timeRange = timeRange
    ) {
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
        calendar.time = it.timeRange.begin
        val subjectDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (oldSubjectDay != subjectDay) {
            val title = DateFormatter.format(context, it.timeRange.begin)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            items.add(ScheduleItem.Title(title, calendar.time))
            oldSubjectDay = subjectDay
        }

        val msBeforeStart = it.timeRange.begin.time - now
        if (msBeforeStart > 0 && msBeforeStart < UiScheduleConstants.ItemLifetime) {
            val start = items.last().timeRange.end
            val end = it.timeRange.begin
            items.add(ScheduleItem.PendingLabel(TimeRange(start, end)))
        }

        val msBeforeEnd = it.timeRange.end.time - now
        if (msBeforeEnd > 0 && msBeforeEnd < UiScheduleConstants.ItemLifetime) {
            items.add(ScheduleItem.ActiveLabel(it.timeRange))
        }

        items.add(
            ScheduleItem.Subject(
                id = it.id,
                index = it.timeRange.getSubjectIndex(calendar),
                timeRange = it.timeRange,
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
    val mins = ((begin.time / 60000L) % (60 * 24)).toInt()
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
            val h = ((begin.time + zoneOffsetMs) / (1000 * 60 * 60L)) % 24

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
