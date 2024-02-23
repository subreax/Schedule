package com.subreax.schedule.di

import android.content.Context
import androidx.room.Room
import com.subreax.schedule.data.local.MIGRATION_1_2
import com.subreax.schedule.data.local.ScheduleDatabase
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
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}