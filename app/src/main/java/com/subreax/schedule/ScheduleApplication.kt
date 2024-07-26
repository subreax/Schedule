package com.subreax.schedule

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
        /*if (BuildConfig.DEBUG) {
            Log.d("ScheduleApplication", "Analytics disabled")
        }*/
    }
}