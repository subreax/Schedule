package com.subreax.schedule.data.local.subjectname.impl

import android.util.Log
import com.subreax.schedule.data.local.ScheduleDatabase
import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.local.subjectname.LocalSubjectNameDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalSubjectNameDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalSubjectNameDataSource {
    private val dao: SubjectNameDao = database.subjectNameDao

    override suspend fun getEntry(id: Int): Resource<LocalSubjectName> {
        return withContext(Dispatchers.IO) {
            val entry = dao.getEntryById(id)
            if (entry != null) {
                Resource.Success(entry)
            } else {
                Resource.Failure(UiText.hardcoded("LocalSubjectName not found for id = $id"))
            }
        }
    }

    override suspend fun setNameAlias(id: Int, name: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dao.setNameAlias(id, name)
                Resource.Success(Unit)
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to set name alias", ex)
                Resource.Failure(UiText.hardcoded("Failed to set name alias"))
            }
        }
    }

    companion object {
        private const val TAG = "LocalSubjectNameDSImpl"
    }
}