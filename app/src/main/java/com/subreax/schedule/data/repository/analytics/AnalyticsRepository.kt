package com.subreax.schedule.data.repository.analytics

interface AnalyticsRepository {
    fun sendUserScheduleId(scheduleId: String)
}