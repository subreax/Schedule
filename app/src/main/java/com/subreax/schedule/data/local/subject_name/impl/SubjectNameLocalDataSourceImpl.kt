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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectNameLocalDataSourceImpl @Inject constructor(
    private val subjectNameDao: SubjectNameDao
) : SubjectNameLocalDataSource {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val memCache = HashMap<Int, SubjectNameEntity>()

    override suspend fun getEntryByName(name: String): SubjectNameEntity {
        return coroutineScope.async {
            val entry = subjectNameDao.getEntryByName(name)
            if (entry != null) {
                return@async entry
            }

            subjectNameDao.addEntry(SubjectNameEntity(0, name, ""))
            subjectNameDao.getEntryByName(name)!!
        }.await()
    }

    override suspend fun getEntryById(id: Int): Resource<SubjectNameEntity> {
        return withContext(Dispatchers.Default) {
            var entity = memCache[id]
            if (entity == null) {
                entity = subjectNameDao.getEntryById(id)?.also {
                    memCache[id] = it
                }
            }

            if (entity != null) {
                Resource.Success(entity)
            } else {
                Resource.Failure(UiText.hardcoded("Subject name not found"))
            }
        }
    }

    override suspend fun setNameAlias(name: String, alias: String): Resource<Unit> {
        return coroutineScope.async {
            val entry = subjectNameDao.getEntryByName(name)
            if (entry != null) {
                subjectNameDao.setNameAlias(name, alias)
                memCache[entry.id] = subjectNameDao.getEntryByName(name)!!
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.hardcoded("Not found"))
            }
        }.await()
    }
}