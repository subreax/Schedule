package com.subreax.schedule.data.local.teacher_name

import com.subreax.schedule.data.local.entitiy.TeacherNameEntity
import com.subreax.schedule.utils.Resource

interface TeacherNameLocalDataSource {
    suspend fun getEntryByName(name: String): TeacherNameEntity
    suspend fun getEntryById(id: Int): Resource<TeacherNameEntity>
}
