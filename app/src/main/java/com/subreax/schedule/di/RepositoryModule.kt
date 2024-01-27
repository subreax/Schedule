package com.subreax.schedule.di

import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import com.subreax.schedule.data.local.owner.impl.LocalOwnerDataSourceImpl
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.local.schedule.impl.LocalScheduleDataSourceImpl
import com.subreax.schedule.data.network.owner.NetworkOwnerDataSource
import com.subreax.schedule.data.network.owner.impl.NetworkOwnerDataSourceImpl
import com.subreax.schedule.data.network.schedule.NetworkScheduleDataSource
import com.subreax.schedule.data.network.schedule.impl.NetworkScheduleDataSourceImpl
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.schedule.impl.ScheduleRepositoryImpl
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.data.repository.scheduleowner.impl.ScheduleOwnerRepositoryImpl
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
    abstract fun bindScheduleOwnerRepository(impl: ScheduleOwnerRepositoryImpl): ScheduleOwnerRepository


    @Binds
    @Singleton
    abstract fun bindNetworkScheduleDataSource(impl: NetworkScheduleDataSourceImpl): NetworkScheduleDataSource

    @Binds
    @Singleton
    abstract fun bindLocalScheduleDataSource(impl: LocalScheduleDataSourceImpl): LocalScheduleDataSource


    @Binds
    @Singleton
    abstract fun bindNetworkOwnerDataSource(impl: NetworkOwnerDataSourceImpl): NetworkOwnerDataSource

    @Binds
    @Singleton
    abstract fun bindLocalOwnerDataSource(impl: LocalOwnerDataSourceImpl): LocalOwnerDataSource
}