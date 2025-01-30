package com.subreax.schedule.data.repository.ac_schedule

import com.subreax.schedule.data.model.AcademicScheduleItem
import com.subreax.schedule.utils.Resource

interface AcademicScheduleRepository {
    suspend fun getAcademicSchedule(id: String): Resource<List<AcademicScheduleItem>>
}
