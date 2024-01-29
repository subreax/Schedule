package com.subreax.schedule.data.repository.schedule

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.repository.schedule.provider.ScheduleProvider
import com.subreax.schedule.utils.Resource

interface ScheduleRepository {
    suspend fun getScheduleProvider(ownerNetworkId: String): Resource<ScheduleProvider>
    suspend fun deleteSchedule(owner: ScheduleOwner): Resource<Unit>
}