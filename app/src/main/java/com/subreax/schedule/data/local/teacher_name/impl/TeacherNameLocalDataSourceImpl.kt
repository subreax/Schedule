package com.subreax.schedule.data.local.teacher_name.impl

import com.subreax.schedule.data.local.dao.TeacherNameDao
import com.subreax.schedule.data.local.entitiy.TeacherNameEntity
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import javax.inject.Inject

class TeacherNameLocalDataSourceImpl @Inject constructor(
    private val teacherNameDao: TeacherNameDao
) : TeacherNameLocalDataSource {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val nameToEntryCache = HashMap<String, TeacherNameEntity>()

    override suspend fun getEntryByName(name: String): TeacherNameEntity {
        return coroutineScope.async {
            val entry = _getEntryByName(name)
            if (entry != null) {
                entry
            } else {
                teacherNameDao.addName(name)
                _getEntryByName(name)!!
            }
        }.await()
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
