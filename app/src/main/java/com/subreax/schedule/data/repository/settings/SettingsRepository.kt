package com.subreax.schedule.data.repository.settings

import com.subreax.schedule.data.model.Settings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<Settings>

    fun update(newSettings: Settings)
}
