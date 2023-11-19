package com.subreax.schedule.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.entitiy.LocalSubject

@Database(entities = [LocalSubject::class], version = 1)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract val subjectDao: SubjectDao
}