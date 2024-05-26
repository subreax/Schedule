package com.subreax.schedule.data.repository.subjectname

import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.utils.Resource

interface SubjectNameRepository {
    suspend fun getNameBySubjectId(subjectId: Long): Resource<LocalSubjectName>
    suspend fun renameSubject(subjectId: Long, newName: String): Resource<Unit>
}