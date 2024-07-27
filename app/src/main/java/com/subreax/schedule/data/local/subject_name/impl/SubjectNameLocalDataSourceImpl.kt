package com.subreax.schedule.data.local.subject_name.impl

import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.entitiy.SubjectNameEntity
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import javax.inject.Inject

class SubjectNameLocalDataSourceImpl @Inject constructor(
    private val subjectNameDao: SubjectNameDao
) : SubjectNameLocalDataSource {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val nameToEntryCache = HashMap<String, SubjectNameEntity>()

    override suspend fun getEntryByName(name: String): SubjectNameEntity {
        return coroutineScope.async {
            val entry = _getEntryByName(name)
            if (entry != null) {
                return@async entry
            }

            subjectNameDao.addEntry(name)
            _getEntryByName(name)!!
        }.await()
    }

    override suspend fun setNameAlias(name: String, alias: String): Resource<Unit> {
        return coroutineScope.async {
            val entry = _getEntryByName(name)
            if (entry != null) {
                subjectNameDao.setNameAlias(name, alias)
                nameToEntryCache[name] = entry.copy(alias = alias)
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.hardcoded("Not found"))
            }
        }.await()
    }

    private suspend fun _getEntryByName(name: String): SubjectNameEntity? {
        val entity = nameToEntryCache[name]
        if (entity != null) {
            return entity
        }

        val dbEntity = subjectNameDao.getEntryByName(name)
        if (dbEntity != null) {
            nameToEntryCache[name] = dbEntity
        }

        return dbEntity
    }
}