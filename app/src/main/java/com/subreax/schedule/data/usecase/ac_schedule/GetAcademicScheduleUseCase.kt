package com.subreax.schedule.data.usecase.ac_schedule

import com.subreax.schedule.data.model.AcademicScheduleItem
import com.subreax.schedule.data.repository.ac_schedule.AcademicScheduleRepository
import com.subreax.schedule.utils.Resource

class GetAcademicScheduleUseCase(private val acScheduleRepository: AcademicScheduleRepository) {
    suspend operator fun invoke(id: String): Resource<List<AcademicScheduleItem>> {
        return acScheduleRepository.getAcademicSchedule(id)
    }
}