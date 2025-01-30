package com.subreax.schedule.data.repository.ac_schedule.impl

import com.subreax.schedule.data.model.AcademicScheduleItem
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.repository.ac_schedule.AcademicScheduleRepository
import com.subreax.schedule.utils.Resource

class AcademicScheduleRepositoryImpl(
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource
) : AcademicScheduleRepository {
    override suspend fun getAcademicSchedule(id: String): Resource<List<AcademicScheduleItem>> {
        return scheduleNetworkDataSource.getAcademicSchedule(id)
    }
}
