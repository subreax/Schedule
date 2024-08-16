package com.subreax.schedule.ui.component.schedule.item

import android.content.Context
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.utils.DateFormatter
import com.subreax.schedule.utils.join
import java.util.Calendar
import java.util.Date

sealed class ScheduleItem(val date: Date) {
    class Subject(
        val id: Long,
        val index: String,
        date: Date,
        val title: String,
        val subtitle: String,
        val type: SubjectType,
        val note: String?
    ) : ScheduleItem(date)

    class Title(val title: String, date: Date) : ScheduleItem(date)
}

fun List<Subject>.toScheduleItems(context: Context, scheduleType: ScheduleType): List<ScheduleItem> {
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
    itemSubtitle: (Subject) -> String,
    itemNote: (Subject) -> String?,
): List<ScheduleItem> {
    val calendar = Calendar.getInstance()
    val items = mutableListOf<ScheduleItem>()
    var oldSubjectDay = -1
    this.forEach {
        val subjectDay = getDayOfMonth(calendar, it.timeRange.start)

        if (oldSubjectDay != subjectDay) {
            val title = DateFormatter.format(context, it.timeRange.start)
            items.add(
                ScheduleItem.Title(
                    title = title,
                    // time of the title should be less than time of the subject
                    date = Date(it.timeRange.start.time - 60000L)
                )
            )
            oldSubjectDay = subjectDay
        }

        items.add(
            ScheduleItem.Subject(
                id = it.id,
                index = it.timeRange.getSubjectIndex(calendar),
                date = it.timeRange.start,
                title = it.nameAlias.ifEmpty { it.name },
                subtitle = itemSubtitle(it),
                type = it.type,
                note = itemNote(it)
            )
        )
    }

    return items
}

private fun buildStudentSubjectItemSubtitle(subject: Subject): String {
    val typeName = if (subject.type.ordinal > 2)
        subject.type.name
    else
        ""

    return " • ".join(subject.place, typeName, subject.teacher?.compact() ?: "")
}

private fun buildTeacherSubjectItemSubtitle(subject: Subject): String {
    val groups = mapGroupsToStrings(subject.groups)
    val typeName = if (subject.type.ordinal > 2)
        subject.type.name
    else
        ""

    return " • ".join(subject.place, typeName, *groups.toTypedArray())
}

private fun buildRoomSubjectItemSubtitle(subject: Subject): String {
    val teacherName = subject.teacher?.compact() ?: ""
    val groups = mapGroupsToStrings(subject.groups)
    val typeName = if (subject.type.ordinal > 2)
        subject.type.name
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

private fun getDayOfMonth(calendar: Calendar, time: Date): Int {
    calendar.time = time
    return calendar.get(Calendar.DAY_OF_MONTH)
}