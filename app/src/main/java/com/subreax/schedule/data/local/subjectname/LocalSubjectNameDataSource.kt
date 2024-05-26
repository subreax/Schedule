package com.subreax.schedule.data.local.subjectname

import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.utils.Resource

interface LocalSubjectNameDataSource {
    suspend fun getEntry(id: Int): Resource<LocalSubjectName>
    suspend fun setNameAlias(id: Int, name: String): Resource<Unit>
}