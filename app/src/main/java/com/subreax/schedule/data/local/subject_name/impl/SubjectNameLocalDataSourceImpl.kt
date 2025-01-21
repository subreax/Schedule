package com.subreax.schedule.data.local.subject_name.impl

import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.entitiy.SubjectNameEntity
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SubjectNameLocalDataSourceImpl(
    private val subjectNameDao: SubjectNameDao,
    private val ioDispatcher: CoroutineDispatcher
) : SubjectNameLocalDataSource {
    private val nameToEntryCache = HashMap<String, SubjectNameEntity>()

    override suspend fun getEntryByName(name: String): SubjectNameEntity {
        return withContext(ioDispatcher) {
            val entry = _getEntryByName(name)
            if (entry != null) {
                entry
            } else {
                subjectNameDao.addEntry(name)
                _getEntryByName(name)!!
            }
        }
    }

    override suspend fun setNameAlias(name: String, alias: String): Resource<Unit> {
        return withContext(ioDispatcher) {
            val entry = _getEntryByName(name)
            if (entry != null) {
                subjectNameDao.setNameAlias(name, alias)
                nameToEntryCache[name] = entry.copy(alias = alias)
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.hardcoded("Not found"))
            }
        }
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