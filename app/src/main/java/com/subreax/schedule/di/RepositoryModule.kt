package com.subreax.schedule.di

import com.subreax.schedule.data.local.LocalDataSource
import com.subreax.schedule.data.local.LocalDataSourceImpl
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.network.impl.NetworkDataSourceImpl
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule.impl.ScheduleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindNetworkDataSource(impl: NetworkDataSourceImpl): NetworkDataSource

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(impl: LocalDataSourceImpl): LocalDataSource
}