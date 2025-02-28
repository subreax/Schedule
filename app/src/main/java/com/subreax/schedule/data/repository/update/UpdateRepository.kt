package com.subreax.schedule.data.repository.update

import com.subreax.schedule.data.model.AppUpdateInfo
import com.subreax.schedule.utils.Resource

interface UpdateRepository {
    suspend fun getLatestRelease(): Resource<AppUpdateInfo>
}
