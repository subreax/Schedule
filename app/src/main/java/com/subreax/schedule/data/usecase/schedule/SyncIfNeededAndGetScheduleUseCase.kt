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

        val schedule = getSchedule(id)
        if (syncRes is Resource.Success) {
            Resource.Success(schedule!!)
        } else {
            Resource.Failure((syncRes as Resource.Failure).message, schedule)
        }
    }

    private suspend fun getSchedule(id: String): Schedule? {
        return (scheduleRepository.get(id) as? Resource.Success)?.value
    }
}
