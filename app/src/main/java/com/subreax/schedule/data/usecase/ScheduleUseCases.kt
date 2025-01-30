package com.subreax.schedule.data.usecase

import com.subreax.schedule.data.usecase.schedule.GetScheduleUseCase
import com.subreax.schedule.data.usecase.schedule.IsScheduleExpiredUseCase
import com.subreax.schedule.data.usecase.schedule.SyncAndGetScheduleUseCase
import com.subreax.schedule.data.usecase.schedule.SyncIfNeededAndGetScheduleUseCase

data class ScheduleUseCases(
    val get: GetScheduleUseCase,
    val isExpired: IsScheduleExpiredUseCase,
    val syncIfNeededAndGet: SyncIfNeededAndGetScheduleUseCase,
    val syncAndGet: SyncAndGetScheduleUseCase
)
