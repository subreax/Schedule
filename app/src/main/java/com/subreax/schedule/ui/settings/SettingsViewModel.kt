package com.subreax.schedule.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Settings
import com.subreax.schedule.data.repository.settings.SettingsRepository
import com.subreax.schedule.data.repository.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    userRepository: UserRepository
) : ViewModel() {
    val settings = settingsRepository.settings

    val showSecretSettings = userRepository.userType.map { it == ScheduleType.Student }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun updateSettings(newSettings: Settings) {
        settingsRepository.update(newSettings)
    }
}