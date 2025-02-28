package com.subreax.schedule.data.usecase.update

import com.subreax.schedule.data.model.AppUpdateInfo
import com.subreax.schedule.data.repository.update.UpdateRepository

class CheckForUpdatesUseCase(
    private val updateRepository: UpdateRepository
) {
    suspend operator fun invoke(buildTime: Long): AppUpdateInfo? {
        return updateRepository.getLatestRelease().getValueOrNull()?.let {
            if (buildTime < it.createdAt) {
                it
            } else {
                null
            }
        }
    }
}
