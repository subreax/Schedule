package com.subreax.schedule.data.repository.update

import com.subreax.schedule.data.model.AppUpdateInfo
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText

class UpdateRepositoryImpl : UpdateRepository {
    override suspend fun getLatestRelease(): Resource<AppUpdateInfo> {
        return Resource.Failure(UiText.hardcoded("This build can't search for updates"))
    }
}
