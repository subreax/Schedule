package com.subreax.schedule.data.repository.schedule

import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface ScheduleRepository {
    suspend fun getSchedule(owner: ScheduleOwner): Resource<Schedule>
    suspend fun deleteSchedule(owner: ScheduleOwner): Resource<Unit>
    suspend fun findSubjectById(id: Long): Subject?
}