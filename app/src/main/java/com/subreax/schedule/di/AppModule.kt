package com.subreax.schedule.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.subreax.schedule.data.local.cache.LocalCache
import com.subreax.schedule.data.local.cache.datastore.DataStoreLocalCache
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.data.local.subject_name.impl.SubjectNameLocalDataSourceImpl
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import com.subreax.schedule.data.local.teacher_name.impl.TeacherNameLocalDataSourceImpl
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.network.schedule.tsu.TsuScheduleNetworkDataSource
import com.subreax.schedule.data.repository.ac_schedule.AcademicScheduleRepository
import com.subreax.schedule.data.repository.ac_schedule.impl.AcademicScheduleRepositoryImpl
import com.subreax.schedule.data.repository.analytics.AnalyticsRepository
import com.subreax.schedule.data.repository.analytics.firebase.FirebaseAnalyticsRepository
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.bookmark.offline.OfflineBookmarkRepository
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule.impl.ScheduleRepositoryImpl
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.data.repository.schedule_id.tsu.TsuScheduleIdRepository
import com.subreax.schedule.data.repository.settings.SettingsRepository
import com.subreax.schedule.data.repository.settings.impl.DataStoreSettingsRepository
import com.subreax.schedule.data.repository.subject.SubjectRepository
import com.subreax.schedule.data.repository.subject.SubjectRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File

val appModule = module {
    single<ScheduleNetworkDataSource> {
        TsuScheduleNetworkDataSource(get(), get(), getIoDispatcher())
    }

    single<LocalCache> {
        fun Context.preferencesDataStoreCacheFile(name: String): File {
            return File(cacheDir, "datastore/$name.preferences_pb")
        }

        val dataStore = PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreCacheFile(DataStoreLocalCache.FILE_NAME)
        }

        DataStoreLocalCache(dataStore)
    }

    single<SettingsRepository>(createdAtStart = true) {
        fun Context.preferencesDataStoreFile(name: String): File {
            return File(filesDir, "datastore/$name.preferences_pb")
        }

        val dataStore = PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile(DataStoreSettingsRepository.FILE_NAME)
        }

        DataStoreSettingsRepository(dataStore, get())
    }

    single<BookmarkRepository> {
        OfflineBookmarkRepository(get(), get(), get(), get(), getIoDispatcher())
    }

    single<ScheduleIdRepository> {
        TsuScheduleIdRepository(get(), getIoDispatcher())
    }

    single<ScheduleNetworkDataSource> {
        TsuScheduleNetworkDataSource(get(), get(), getIoDispatcher())
    }

    single<SubjectNameLocalDataSource> {
        SubjectNameLocalDataSourceImpl(get(), getIoDispatcher())
    }

    single<TeacherNameLocalDataSource> {
        TeacherNameLocalDataSourceImpl(get(), getIoDispatcher())
    }

    single<SubjectRepository> {
        SubjectRepositoryImpl(get(), get(), get(), getDefaultDispatcher())
    }

    single<ScheduleRepository> {
        ScheduleRepositoryImpl(get(), get(), get(), get(), get(), get())
    }

    single<AnalyticsRepository> {
        FirebaseAnalyticsRepository(get(), get())
    }

    single<AcademicScheduleRepository> {
        AcademicScheduleRepositoryImpl(get())
    }
}

