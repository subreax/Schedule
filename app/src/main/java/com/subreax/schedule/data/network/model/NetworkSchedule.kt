package com.subreax.schedule.data.network.model

data class NetworkSchedule(
    val id: String,
    val type: NetworkScheduleType,
    val subjects: List<NetworkSubject>
)
