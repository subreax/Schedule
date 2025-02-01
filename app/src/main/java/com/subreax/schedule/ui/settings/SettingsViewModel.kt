package com.subreax.schedule.ui.settings

import androidx.lifecycle.ViewModel
import com.subreax.schedule.data.model.Settings
import com.subreax.schedule.data.repository.settings.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings = settingsRepository.settings

    fun updateSettings(newSettings: Settings) {
        settingsRepository.update(newSettings)
    }
}