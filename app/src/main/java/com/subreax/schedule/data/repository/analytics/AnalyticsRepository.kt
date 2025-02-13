package com.subreax.schedule.data.repository.analytics

interface AnalyticsRepository {
    fun sendUserScheduleId(scheduleId: String)
    fun recordException(throwable: Throwable, keys: Map<String, String> = emptyMap())
    fun setEnabled(enabled: Boolean)
}