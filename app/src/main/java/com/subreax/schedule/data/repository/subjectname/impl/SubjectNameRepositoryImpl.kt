package com.subreax.schedule.data.repository.subjectname.impl

import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.local.subjectname.LocalSubjectNameDataSource
import com.subreax.schedule.data.repository.subjectname.SubjectNameRepository
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectNameRepositoryImpl @Inject constructor(
    private val localScheduleDataSource: LocalScheduleDataSource,
    private val localSubjectNameDataSource: LocalSubjectNameDataSource
) : SubjectNameRepository {
    override suspend fun getNameBySubjectId(subjectId: Long): Resource<LocalSubjectName> {
        return withContext(Dispatchers.Default) {
            val res = localScheduleDataSource.getSubjectNameId(subjectId)
            if (res is Resource.Failure) {
                return@withContext Resource.Failure(res.message)
            }

            val nameId = (res as Resource.Success).value
            val entryRes = localSubjectNameDataSource.getEntry(nameId)
            if (entryRes is Resource.Failure) {
                return@withContext Resource.Failure(entryRes.message)
            }

            Resource.Success((entryRes as Resource.Success).value)
        }
    }

    override suspend fun renameSubject(subjectId: Long, newName: String): Resource<Unit> {
        return withContext(Dispatchers.Default) {
            val res = localScheduleDataSource.getSubjectNameId(subjectId)
            if (res is Resource.Success) {
                val nameId = res.value
                localSubjectNameDataSource.setNameAlias(nameId, newName.trim())
                Resource.Success(Unit)
            } else {
                Resource.Failure((res as Resource.Failure).message)
            }
        }
    }
}