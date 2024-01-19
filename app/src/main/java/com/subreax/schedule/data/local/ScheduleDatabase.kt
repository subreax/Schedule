package com.subreax.schedule.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.subreax.schedule.data.local.dao.OwnerDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.dao.TeacherNameDao
import com.subreax.schedule.data.local.entitiy.LocalOwner
import com.subreax.schedule.data.local.entitiy.LocalSubject
import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.local.entitiy.LocalTeacherName

@Database(
    entities = [
        LocalSubject::class,
        LocalSubjectName::class,
        LocalTeacherName::class,
        LocalOwner::class
    ],
    version = 1
)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract val subjectDao: SubjectDao
    abstract val subjectNameDao: SubjectNameDao
    abstract val teacherNameDao: TeacherNameDao
    abstract val ownerDao: OwnerDao
}