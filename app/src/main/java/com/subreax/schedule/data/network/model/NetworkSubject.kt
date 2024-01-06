package com.subreax.schedule.data.network.model

import com.subreax.schedule.data.model.PersonName
import java.util.Date

data class NetworkSubject(
    val name: String,
    val place: String,
    val beginTime: Date,
    val endTime: Date,
    val teacher: PersonName?,
    val type: String,
    val kow: String,
    val note: String?
)