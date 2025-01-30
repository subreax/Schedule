package com.subreax.schedule.data.usecase.schedule

import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class SyncIfNeededAndGetScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val isScheduleExpired: IsScheduleExpiredUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(id: String): Resource<Schedule> = withContext(defaultDispatcher) {
        val syncRes = if (isScheduleExpired(id)) {
            scheduleRepository.sync(id)
        } else {
            Resource.Success(Unit)
        }

        ensureActive()

        syncRes.ifSuccess { scheduleRepository.get(id) }
    }
}
