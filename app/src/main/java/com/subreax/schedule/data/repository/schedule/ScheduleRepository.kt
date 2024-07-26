package com.subreax.schedule.data.repository.schedule

import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface ScheduleRepository {
    suspend fun getSchedule(id: String): Resource<Schedule>
    suspend fun getSubjectById(id: Long): Resource<Subject>
    suspend fun setSubjectNameAlias(subjectName: String, nameAlias: String): Resource<Unit>
    suspend fun clearCache(id: String): Resource<Unit>
}