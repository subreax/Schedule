package com.subreax.schedule.data.network.model

data class NetworkSchedule(
    val id: String,
    val type: NetworkOwnerType,
    val subjects: List<NetworkSubject>
)
