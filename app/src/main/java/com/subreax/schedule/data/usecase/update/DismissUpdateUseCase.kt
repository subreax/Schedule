package com.subreax.schedule.data.usecase.update

import com.subreax.schedule.data.local.cache.LocalCache

class DismissUpdateUseCase(
    private val cache: LocalCache
) {
    suspend operator fun invoke(version: String) {
        cache.set("AppUpdate/dismissedVersion", version)
    }
}
