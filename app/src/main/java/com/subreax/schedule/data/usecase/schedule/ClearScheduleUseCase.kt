package com.subreax.schedule.data.usecase.schedule

import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource

class ClearScheduleUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return scheduleRepository.clear(id)
    }
}
