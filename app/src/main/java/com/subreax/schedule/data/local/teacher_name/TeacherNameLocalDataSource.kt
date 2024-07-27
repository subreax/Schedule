package com.subreax.schedule.data.local.teacher_name

import com.subreax.schedule.data.local.entitiy.TeacherNameEntity

interface TeacherNameLocalDataSource {
    suspend fun getEntryByName(name: String): TeacherNameEntity
}
