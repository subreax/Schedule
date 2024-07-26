package com.subreax.schedule.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.subreax.schedule.data.local.cache.datastore.DataStoreLocalCache
import com.subreax.schedule.data.local.cache.LocalCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalCacheModule {
    @Provides
    @Singleton
    fun provideLocalCache(@ApplicationContext context: Context): LocalCache {
        val dataStore = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreCacheFile(DataStoreLocalCache.FILE_NAME)
        }

        return DataStoreLocalCache(dataStore)
    }

    private fun Context.preferencesDataStoreCacheFile(name: String): File {
        return File(applicationContext.cacheDir, "datastore/$name.preferences_pb")
    }
}
