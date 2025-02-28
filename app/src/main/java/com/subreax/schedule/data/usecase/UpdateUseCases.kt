package com.subreax.schedule.data.usecase

import com.subreax.schedule.data.usecase.update.CheckForUpdatesUseCase
import com.subreax.schedule.data.usecase.update.DismissUpdateUseCase
import com.subreax.schedule.data.usecase.update.IsUpdateNewUseCase

data class UpdateUseCases(
    val checkForUpdates: CheckForUpdatesUseCase,
    val isUpdateNew: IsUpdateNewUseCase,
    val dismissUpdate: DismissUpdateUseCase
)
