package com.subreax.schedule.di

import android.content.Context
import androidx.room.Room
import com.subreax.schedule.data.local.MIGRATION_1_2
import com.subreax.schedule.data.local.MIGRATION_1_3
import com.subreax.schedule.data.local.MIGRATION_2_3
import com.subreax.schedule.data.local.MIGRATION_4_5
import com.subreax.schedule.data.local.MIGRATION_5_6
import com.subreax.schedule.data.local.ScheduleDatabase
import com.subreax.schedule.data.local.dao.BookmarkDao
import com.subreax.schedule.data.local.dao.ScheduleIdDao
import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.dao.SubjectNameDao
import com.subreax.schedule.data.local.dao.TeacherNameDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScheduleDatabase {
        return Room
            .databaseBuilder(context, ScheduleDatabase::class.java, "schedule")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_1_3,
                MIGRATION_2_3,
                MIGRATION_4_5,
                MIGRATION_5_6
            )
            .build()
    }

    @Provides
    fun provideSubjectDao(db: ScheduleDatabase): SubjectDao {
        return db.subjectDao
    }

    @Provides
    fun provideBookmarkDao(db: ScheduleDatabase): BookmarkDao {
        return db.bookmarkDao
    }

    @Provides
    fun provideSubjectNameDao(db: ScheduleDatabase): SubjectNameDao {
        return db.subjectNameDao
    }

    @Provides
    fun provideTeacherNameDao(db: ScheduleDatabase): TeacherNameDao {
        return db.teacherNameDao
    }

    @Provides
    fun provideScheduleIdDao(db: ScheduleDatabase): ScheduleIdDao {
        return db.scheduleIdDao
    }
}