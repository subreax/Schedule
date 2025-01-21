package com.subreax.schedule

import android.app.Application
import com.subreax.schedule.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ScheduleApplication)
            modules(KoinModules)
        }
    }
}