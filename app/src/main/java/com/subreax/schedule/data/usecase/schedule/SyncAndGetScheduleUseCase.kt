package com.subreax.schedule.data.usecase.schedule

import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class SyncAndGetScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(id: String): Resource<Schedule> = withContext(defaultDispatcher) {
        scheduleRepository.sync(id).ifSuccess {
            ensureActive()
            scheduleRepository.get(id)
        }
    }
}
