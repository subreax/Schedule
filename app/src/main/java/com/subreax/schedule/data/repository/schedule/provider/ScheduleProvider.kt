package com.subreax.schedule.data.repository.schedule.provider

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface ScheduleProvider {
    fun getOwner(): ScheduleOwner
    suspend fun getSubjects(): Resource<List<Subject>>
    suspend fun getSubjectById(id: Long): Subject?
}