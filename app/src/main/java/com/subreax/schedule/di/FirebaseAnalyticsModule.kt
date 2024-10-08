package com.subreax.schedule.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAnalyticsModule {
    @Provides
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }
}
