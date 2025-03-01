package com.subreax.schedule.data.usecase.update

import com.subreax.schedule.data.model.AppUpdateInfo
import com.subreax.schedule.data.repository.update.UpdateRepository
import com.subreax.schedule.utils.DateTimeUtils

class CheckForUpdatesUseCase(
    private val updateRepository: UpdateRepository
) {
    suspend operator fun invoke(buildTime: Long): AppUpdateInfo? {
        return updateRepository.getLatestRelease().getValueOrNull()?.let {
            val buildDate = DateTimeUtils.keepDateAndRemoveTime(buildTime)
            val releaseDate = DateTimeUtils.keepDateAndRemoveTime(it.createdAt)
            if (buildDate < releaseDate) {
                it
            } else {
                null
            }
        }
    }
}
