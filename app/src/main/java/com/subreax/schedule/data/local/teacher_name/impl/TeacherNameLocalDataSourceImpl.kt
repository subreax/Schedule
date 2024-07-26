package com.subreax.schedule.data.local.teacher_name.impl

import com.subreax.schedule.data.local.dao.TeacherNameDao
import com.subreax.schedule.data.local.entitiy.TeacherNameEntity
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TeacherNameLocalDataSourceImpl @Inject constructor(
    private val teacherNameDao: TeacherNameDao
) : TeacherNameLocalDataSource {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val memCache = HashMap<Int, TeacherNameEntity>()

    override suspend fun getEntryByName(name: String): TeacherNameEntity {
        return coroutineScope.async {
            val entry = teacherNameDao.getEntryByName(name)
            if (entry != null) {
                entry
            } else {
                teacherNameDao.addName(TeacherNameEntity(0, name))
                teacherNameDao.getEntryByName(name)!!
            }
        }.await()
    }

    override suspend fun getEntryById(id: Int): Resource<TeacherNameEntity> {
        return withContext(Dispatchers.IO) {
            var entry = memCache[id]
            if (entry == null) {
                entry = teacherNameDao.getEntryById(id)?.also {
                    memCache[id] = it
                }
            }

            if (entry != null) {
                Resource.Success(entry)
            } else {
                Resource.Failure(UiText.hardcoded("Not found"))
            }
        }
    }
}
