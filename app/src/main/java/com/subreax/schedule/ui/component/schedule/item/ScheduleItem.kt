package com.subreax.schedule.ui.component.schedule.item

import android.content.Context
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.ui.component.schedule.UiScheduleConstants
import com.subreax.schedule.utils.DateFormatter
import com.subreax.schedule.utils.DateTimeUtils
import com.subreax.schedule.utils.join
import com.subreax.schedule.utils.toLocalizedString
import java.util.Date

sealed class ScheduleItem(val key: Long, val timeRange: TimeRange) {
    enum class State {
        Pending, Active, Expired
    }

    val state: State
        get() {
            val t = now
            return if (t < timeRange.begin.time) {
                State.Pending
            } else if (t > timeRange.end.time) {
                State.Expired
            } else {
                State.Active
            }
        }

    private val now: Long
        get() = System.currentTimeMillis()

    fun getMinutesLeftOrZero(inclusive: Boolean = false): Int {
        val incr = if (inclusive) 1 else 0
        val minutesLeft = ((timeRange.end.time - now) / 60000 + incr).toInt().coerceAtLeast(0)
        return if (state == State.Active) minutesLeft else 0
    }

    class Title(val title: String, date: Date) : ScheduleItem(
        key = date.time,
        timeRange = TimeRange(date, Date(date.time + 60000L * 60 * 24))
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
    ) : ScheduleItem(key = id, timeRange = timeRange) {
        companion object {
            const val ContentType = 2
        }
    }

    class ActiveLabel(timeRange: TimeRange) : ScheduleItem(
        key = timeRange.begin.time + 10,
        timeRange = timeRange
    ) {
        companion object {
            const val ContentType = 3
        }
    }

    class PendingLabel(timeRange: TimeRange) : ScheduleItem(
        key = timeRange.begin.time - 10, // главное чтобы ключ был отличен от timeRange.begin
        timeRange = timeRange
    ) {
        companion object {
            const val ContentType = 4
        }
    }

    class Mark(timeRange: TimeRange) : ScheduleItem(
        key = timeRange.begin.time - 15,
        timeRange = timeRange
    ) {
        companion object {
            const val ContentType = 5
        }
    }
}

data class ScheduleItems(
    val items: List<ScheduleItem>,
    val todayItemIndex: Int
)

fun List<Subject>.toScheduleItems(
    context: Context,
    scheduleType: ScheduleType,
    alwaysShowSubjectBeginTime: Boolean
): ScheduleItems {
    return when (scheduleType) {
        ScheduleType.Student,
        ScheduleType.Unknown -> {
            toScheduleItems(
                context = context,
                itemSubtitle = ::buildStudentSubjectItemSubtitle,
                itemNote = {
                    it.groups.first().note
                },
                alwaysShowSubjectBeginTime = alwaysShowSubjectBeginTime
            )
        }

        ScheduleType.Teacher -> {
            toScheduleItems(
                context = context,
                itemSubtitle = ::buildTeacherSubjectItemSubtitle,
                itemNote = { null },
                alwaysShowSubjectBeginTime = alwaysShowSubjectBeginTime
            )
        }

        ScheduleType.Room -> {
            toScheduleItems(
                context = context,
                itemSubtitle = ::buildRoomSubjectItemSubtitle,
                itemNote = { null },
                alwaysShowSubjectBeginTime = alwaysShowSubjectBeginTime
            )
        }
    }
}

private fun List<Subject>.toScheduleItems(
    context: Context,
    itemSubtitle: (Context, Subject) -> String,
    itemNote: (Subject) -> String?,
    alwaysShowSubjectBeginTime: Boolean
): ScheduleItems {
    val now = System.currentTimeMillis()
    val nowDate = DateTimeUtils.keepDateAndRemoveTime(now)
    val items = mutableListOf<ScheduleItem>()
    var oldSubjectDate = 0L
    var todayItemIndex = -1
    this.forEach {
        val subjectDate = DateTimeUtils.keepDateAndRemoveTime(it.timeRange.begin.time)

        if (oldSubjectDate != subjectDate) {
            if (todayItemIndex == -1 && subjectDate >= nowDate) {
                val begin = Date(subjectDate)
                val end = Date(begin.time + 1000)
                items.add(ScheduleItem.Mark(TimeRange(begin, end)))
                todayItemIndex = items.lastIndex
            }

            items.add(
                ScheduleItem.Title(
                    title = DateFormatter.format(context, it.timeRange.begin),
                    date = Date(subjectDate)
                )
            )
            oldSubjectDate = subjectDate
        }

        val msBeforeStart = it.timeRange.begin.time - now
        if (msBeforeStart > 0 && msBeforeStart < UiScheduleConstants.ItemLifetime) {
            val prevItem = items.last()
            val start = if (prevItem is ScheduleItem.Title) {
                prevItem.timeRange.begin // потому что заголовок активен весь день, его конец это уже начало следующего дня
            } else {
                prevItem.timeRange.end
            }

            val end = it.timeRange.begin
            items.add(ScheduleItem.PendingLabel(TimeRange(start, end)))
        }

        val msBeforeEnd = it.timeRange.end.time - now
        if (msBeforeEnd > 0 && msBeforeEnd < UiScheduleConstants.ItemLifetime) {
            items.add(ScheduleItem.ActiveLabel(it.timeRange))
        }


        val subjectBeginTimeMins = it.timeRange.begin.time / 60000L
        val index = if (!alwaysShowSubjectBeginTime) {
            getSubjectIndex(subjectBeginTimeMins)
        } else {
            null
        } ?: getSubjectBeginTime(subjectBeginTimeMins, DateTimeUtils.timezoneOffset)

        items.add(
            ScheduleItem.Subject(
                id = it.id,
                index = index,
                timeRange = it.timeRange,
                title = it.nameAlias.ifEmpty { it.name },
                subtitle = itemSubtitle(context, it),
                type = it.type,
                note = itemNote(it)
            )
        )
    }

    if (todayItemIndex == -1) {
        todayItemIndex = items.lastIndexOf { it is ScheduleItem.Title }
    }

    return ScheduleItems(items, todayItemIndex)
}

private inline fun <T> List<T>.lastIndexOf(predicate: (T) -> Boolean): Int {
    var i = lastIndex
    while (i >= 0) {
        if (predicate(get(i))) {
            return i
        }
        i--
    }
    return i
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

private fun getSubjectIndex(beginTimeMins: Long): String? {
    val minsOfDay = (beginTimeMins % (60 * 24)).toInt()
    return when (minsOfDay) {
        gmt3MinutesOf(7, 45) -> "1"
        gmt3MinutesOf(9, 40) -> "2"
        gmt3MinutesOf(11, 35) -> "3"
        gmt3MinutesOf(13, 40) -> "4"
        gmt3MinutesOf(15, 35) -> "5"
        gmt3MinutesOf(17, 30) -> "6"
        else -> null
    }
}

private fun getSubjectBeginTime(beginTimeMins: Long, timezoneOffsetMs: Int): String {
    val beginTimeMinsTimezone = beginTimeMins + (timezoneOffsetMs / 60000L)
    val m = beginTimeMinsTimezone % 60
    val h = (beginTimeMinsTimezone / 60) % 24

    val mm = if (m >= 10) "$m" else "0$m"
    val hh = if (h >= 10) "$h" else "0$h"
    return "$hh\n$mm"
}

/** Returns total minutes of time since the beginning of the day.
It should be passed in GMT+3 */
private fun gmt3MinutesOf(hours: Int, mins: Int): Int {
    return (hours - 3) * 60 + mins
}
