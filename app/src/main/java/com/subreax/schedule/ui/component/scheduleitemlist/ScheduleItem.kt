package com.subreax.schedule.ui.component.scheduleitemlist

import android.content.Context
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.utils.DateFormatter
import com.subreax.schedule.utils.join
import java.util.Calendar
import java.util.Date

sealed class ScheduleItem {
    data class Subject(
        val id: Long,
        val index: String,
        val title: String,
        val subtitle: String,
        val type: SubjectType,
        val note: String?
    ) : ScheduleItem()

    data class Title(val title: String) : ScheduleItem()
}

fun List<Subject>.toScheduleItems(context: Context, ownerType: ScheduleOwner.Type): List<ScheduleItem> {
    return when (ownerType) {
        ScheduleOwner.Type.Student -> {
            toScheduleItems(
                context = context,
                itemSubtitle = {
                    val typeName = if (it.type.ordinal > 2)
                        it.type.name
                    else
                        ""

                    " • ".join(it.place, typeName, it.teacher?.compact() ?: "")
                },
                itemNote = {
                    it.groups.first().note.ifEmpty { null }
                }
            )
        }

        ScheduleOwner.Type.Teacher -> {
            toScheduleItems(
                context = context,
                itemSubtitle = {
                    val groups = mapGroupsToStrings(it.groups)
                    val typeName = if (it.type.ordinal > 2)
                        it.type.name
                    else
                        ""

                    " • ".join(it.place, typeName, *groups.toTypedArray())
                },
                itemNote = { null }
            )
        }

        ScheduleOwner.Type.Room -> {
            toScheduleItems(
                context = context,
                itemSubtitle = {
                    val teacherName = it.teacher?.compact() ?: ""
                    val groups = mapGroupsToStrings(it.groups)
                    val typeName = if (it.type.ordinal > 2)
                        it.type.name
                    else
                        ""

                    " • ".join(teacherName, typeName, *groups.toTypedArray())
                },
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
            items.add(ScheduleItem.Title(title))
            oldSubjectDay = subjectDay
        }

        items.add(
            ScheduleItem.Subject(
                id = it.id,
                index = it.timeRange.getSubjectIndex(calendar),
                title = it.name,
                subtitle = itemSubtitle(it),
                type = it.type,
                note = itemNote(it)
            )
        )
    }

    return items
}

private fun mapGroupsToStrings(groups: List<Group>): List<String> {
    return groups.map {
        if (it.note.isEmpty()) {
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