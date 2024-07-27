package com.subreax.schedule.data.local.subject_name

import com.subreax.schedule.data.local.entitiy.SubjectNameEntity
import com.subreax.schedule.utils.Resource

interface SubjectNameLocalDataSource {
    suspend fun getEntryByName(name: String): SubjectNameEntity
    suspend fun setNameAlias(name: String, alias: String): Resource<Unit>
}