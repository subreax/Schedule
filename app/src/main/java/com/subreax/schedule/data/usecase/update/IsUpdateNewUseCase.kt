package com.subreax.schedule.data.usecase.update

import com.subreax.schedule.data.local.cache.LocalCache

class IsUpdateNewUseCase(
    private val cache: LocalCache
) {
    suspend operator fun invoke(version: String): Boolean {
        return cache.get("AppUpdate/dismissedVersion", "") != version
    }
}
