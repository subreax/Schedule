package com.subreax.schedule.data.repository.subject

import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.utils.Resource
import java.util.Date

interface SubjectRepository {
    suspend fun replaceSubjects(localScheduleId: Int, networkSubjects: List<NetworkSubject>, clearFrom: Date)
    suspend fun clearSubjects(localScheduleId: Int)
    suspend fun getSubjects(localScheduleId: Int): List<Subject>
    suspend fun getSubjectById(id: Long): Subject?
    suspend fun setSubjectNameAlias(name: String, alias: String): Resource<Unit>
}
