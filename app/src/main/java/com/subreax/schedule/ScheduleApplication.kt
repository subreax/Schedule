package com.subreax.schedule

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.subreax.schedule.data.model.Settings
import com.subreax.schedule.data.repository.settings.SettingsRepository
import com.subreax.schedule.di.KoinModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ScheduleApplication : Application() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ScheduleApplication)
            modules(KoinModules)
        }

        coroutineScope.launch {
            val settings: SettingsRepository by inject()
            settings.settings.map { it.appTheme }
                .distinctUntilChanged()
                .collect {
                    setThemeCompat(it)
                }
        }
    }

    private fun setThemeCompat(theme: Settings.AppTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setTheme31AndAbove(theme)
        } else {
            setTheme30AndBelow(theme)
        }
    }

    private fun setTheme30AndBelow(theme: Settings.AppTheme) {
        val mode = when (theme) {
            Settings.AppTheme.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            Settings.AppTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
            Settings.AppTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setTheme31AndAbove(theme: Settings.AppTheme) {
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val mode = when (theme) {
            Settings.AppTheme.System -> UiModeManager.MODE_NIGHT_AUTO
            Settings.AppTheme.Light -> UiModeManager.MODE_NIGHT_NO
            Settings.AppTheme.Dark -> UiModeManager.MODE_NIGHT_YES
        }
        uiModeManager.setApplicationNightMode(mode)
    }
}