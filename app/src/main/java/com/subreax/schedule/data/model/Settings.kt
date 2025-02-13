package com.subreax.schedule.data.model

data class Settings(
    val appTheme: AppTheme = DefaultAppTheme,
    val alwaysShowSubjectBeginTime: Boolean = DefaultShowSubjectBeginTime,
    val scheduleLifetimeMs: Long = DefaultScheduleLifetimeMs,
    val hideLectures: Boolean = DefaultHideLectures
) {
    enum class AppTheme {
        System, Light, Dark
    }

    companion object {
        val DefaultAppTheme = AppTheme.System
        const val DefaultShowSubjectBeginTime = false
        const val DefaultScheduleLifetimeMs = 60000L * 30
        const val DefaultHideLectures = false
    }
}