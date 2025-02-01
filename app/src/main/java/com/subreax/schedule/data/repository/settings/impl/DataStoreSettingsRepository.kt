package com.subreax.schedule.data.repository.settings.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.subreax.schedule.data.model.Settings
import com.subreax.schedule.data.repository.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val externalScope: CoroutineScope
) : SettingsRepository {
    private val _settings = MutableStateFlow(Settings())
    override val settings = _settings.asStateFlow()

    init {
        externalScope.launch {
            val prefs = dataStore.data.first()
            _settings.value = Settings(
                appTheme = prefs.getAppTheme(),
                alwaysShowSubjectBeginTime = prefs.getAlwaysShowSubjectBeginTime(),
                scheduleLifetimeMs = prefs.getScheduleLifetime()
            )
        }
    }

    override fun update(newSettings: Settings) {
        _settings.value = newSettings

        externalScope.launch {
            val appThemeKey = stringPreferencesKey(KEY_APP_THEME)
            val showSubjectBeginTimeKey = booleanPreferencesKey(KEY_ALWAYS_SHOW_SUBJECT_BEGIN_TIME)
            val scheduleLifetimeMsKey = longPreferencesKey(KEY_SCHEDULE_LIFETIME_MS)

            dataStore.edit { prefs ->
                prefs[appThemeKey] = newSettings.appTheme.toString()
                prefs[showSubjectBeginTimeKey] = newSettings.alwaysShowSubjectBeginTime
                prefs[scheduleLifetimeMsKey] = newSettings.scheduleLifetimeMs
            }
        }
    }

    private fun Preferences.getAppTheme(): Settings.AppTheme {
        val key = stringPreferencesKey(KEY_APP_THEME)
        return get(key)?.let { Settings.AppTheme.valueOf(it) } ?: Settings.DefaultAppTheme
    }

    private fun Preferences.getScheduleLifetime(): Long {
        val key = longPreferencesKey(KEY_SCHEDULE_LIFETIME_MS)
        return getOr(key, Settings.DefaultScheduleLifetimeMs)
    }

    private fun Preferences.getAlwaysShowSubjectBeginTime(): Boolean {
        val key = booleanPreferencesKey(KEY_ALWAYS_SHOW_SUBJECT_BEGIN_TIME)
        return getOr(key, Settings.DefaultShowSubjectBeginTime)
    }

    private fun <T> Preferences.getOr(key: Preferences.Key<T>, defaultValue: T): T {
        return get(key) ?: defaultValue
    }

    companion object {
        const val FILE_NAME = "settings"

        private const val KEY_APP_THEME = "app_theme"
        private const val KEY_SCHEDULE_LIFETIME_MS = "schedule_lifetime"
        private const val KEY_ALWAYS_SHOW_SUBJECT_BEGIN_TIME = "always_show_subject_begin_time"
    }
}