package com.subreax.schedule.di

import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import com.subreax.schedule.data.local.teacher_name.impl.TeacherNameLocalDataSourceImpl
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.network.schedule.tsu.TsuScheduleNetworkDataSource
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.bookmark.offline.OfflineBookmarkRepository
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule.offline.OfflineFirstScheduleRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.data.repository.schedule_id.tsu.TsuScheduleIdRepository
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.data.local.subject_name.impl.SubjectNameLocalDataSourceImpl
import com.subreax.schedule.data.repository.analytics.AnalyticsRepository
import com.subreax.schedule.data.repository.analytics.firebase.FirebaseAnalyticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: OfflineFirstScheduleRepository): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindScheduleIdRepository(impl: TsuScheduleIdRepository): ScheduleIdRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(impl: OfflineBookmarkRepository): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindSubjectNameLocalDataSource(impl: SubjectNameLocalDataSourceImpl): SubjectNameLocalDataSource

    @Binds
    @Singleton
    abstract fun bindTeacherNameLocalDataSource(impl: TeacherNameLocalDataSourceImpl): TeacherNameLocalDataSource

    @Binds
    @Singleton
    abstract fun bindScheduleNetworkDataSource(impl: TsuScheduleNetworkDataSource): ScheduleNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(impl: FirebaseAnalyticsRepository): AnalyticsRepository
}