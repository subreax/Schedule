package com.subreax.schedule.data.local.schedule

import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface LocalScheduleDataSource {
    suspend fun updateSchedule(scheduleOwner: String, schedule: List<Subject>)
    suspend fun loadSchedule(scheduleOwner: String): Resource<List<LocalExpandedSubject>>
    suspend fun deleteSchedule(scheduleOwner: String): Resource<Unit>
    suspend fun findSubjectById(id: Int): LocalExpandedSubject?
}