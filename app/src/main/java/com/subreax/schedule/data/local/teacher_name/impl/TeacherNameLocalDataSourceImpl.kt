package com.subreax.schedule.data.local.teacher_name.impl

import com.subreax.schedule.data.local.dao.TeacherNameDao
import com.subreax.schedule.data.local.entitiy.TeacherNameEntity
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TeacherNameLocalDataSourceImpl(
    private val teacherNameDao: TeacherNameDao,
    private val ioDispatcher: CoroutineDispatcher
) : TeacherNameLocalDataSource {
    private val nameToEntryCache = HashMap<String, TeacherNameEntity>()

    override suspend fun getEntryByName(name: String): TeacherNameEntity {
        return withContext(ioDispatcher) {
            val entry = _getEntryByName(name)
            if (entry != null) {
                entry
            } else {
                teacherNameDao.addName(name)
                _getEntryByName(name)!!
            }
        }
    }

    private suspend fun _getEntryByName(name: String): TeacherNameEntity? {
        val entry = nameToEntryCache[name]
        if (entry != null) {
            return entry
        }

        val dbEntry = teacherNameDao.getEntryByName(name)
        if (dbEntry != null) {
            nameToEntryCache[name] = dbEntry
        }

        return dbEntry
    }
}
