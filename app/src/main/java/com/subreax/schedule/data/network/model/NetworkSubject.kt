package com.subreax.schedule.data.network.model

import java.util.Date

data class NetworkSubject(
    val name: String,
    val place: String,
    val beginTime: Date,
    val endTime: Date,
    val teacher: String?,
    val type: String,
    val groups: List<NetworkGroup>
)