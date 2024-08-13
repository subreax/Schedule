package com.subreax.schedule

import android.app.Application
import android.util.Log
import com.google.firebase.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.d("ScheduleApplication", "Analytics disabled")
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
        }
    }
}