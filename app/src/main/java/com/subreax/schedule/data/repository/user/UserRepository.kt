package com.subreax.schedule.data.repository.user

import com.subreax.schedule.data.model.ScheduleType
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val userType: StateFlow<ScheduleType>
}