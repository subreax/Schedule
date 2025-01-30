package com.subreax.schedule.data.usecase.schedule

import com.subreax.schedule.data.repository.schedule.ScheduleRepository

class IsScheduleExpiredUseCase(private val scheduleRepository: ScheduleRepository) {
    suspend operator fun invoke(id: String): Boolean {
        val syncTime = scheduleRepository.getSyncTime(id)
        val now = System.currentTimeMillis()

        return now - syncTime.time >= SCHEDULE_LIFETIME
    }

    companion object {
        private const val SCHEDULE_LIFETIME = 60000L * 30
    }
}