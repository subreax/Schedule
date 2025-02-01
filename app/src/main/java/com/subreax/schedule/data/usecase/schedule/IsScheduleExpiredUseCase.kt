package com.subreax.schedule.data.usecase.schedule

import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.settings.SettingsRepository

class IsScheduleExpiredUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository
) {
    private val scheduleLifetimeMs: Long
        get() = settingsRepository.settings.value.scheduleLifetimeMs

    suspend operator fun invoke(id: String): Boolean {
        val syncTime = scheduleRepository.getSyncTime(id)
        val now = System.currentTimeMillis()

        return now - syncTime.time >= scheduleLifetimeMs
    }
}