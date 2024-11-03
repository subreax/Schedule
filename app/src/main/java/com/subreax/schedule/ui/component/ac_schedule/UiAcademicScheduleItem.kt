package com.subreax.schedule.ui.component.ac_schedule

sealed class UiAcademicScheduleItem(
    val title: String,
    val begin: String,
    val end: String
) {
    class Pending(
        title: String,
        begin: String,
        end: String,
        val daysBeforeStart: Int
    ) : UiAcademicScheduleItem(title, begin, end)

    class Active(
        title: String,
        begin: String,
        end: String,
        val progress: Float,
        val daysRemaining: Int
    ) : UiAcademicScheduleItem(title, begin, end)

    class Finished(
        title: String,
        begin: String,
        end: String
    ) : UiAcademicScheduleItem(title, begin, end)
}
