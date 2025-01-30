package com.subreax.schedule.data.usecase.schedule

import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource

class GetScheduleUseCase(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(id: String): Resource<Schedule> {
        return scheduleRepository.get(id)
    }
}
