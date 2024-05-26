package com.subreax.schedule.data.local.schedule

import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.Resource

interface LocalScheduleDataSource {
    suspend fun insertSubjects(ownerNetworkId: String, schedule: List<Subject>)
    suspend fun loadSchedule(ownerNetworkId: String, minSubjectEndTime: Long): Resource<List<Subject>>
    suspend fun deleteSubjectsAfterSpecifiedTime(ownerNetworkId: String, minSubjectEndTime: Long)
    suspend fun deleteSchedule(ownerNetworkId: String): Resource<Unit>
    suspend fun findSubjectById(id: Long): Subject?
    suspend fun hasSubjects(ownerNetworkId: String): Boolean
    suspend fun getSubjectNameId(subjectId: Long): Resource<Int>
}