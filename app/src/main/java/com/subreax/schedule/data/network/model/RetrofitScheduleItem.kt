package com.subreax.schedule.data.network.model

data class RetrofitScheduleItem(
    val DATE_Z: String,
    val TIME_Z: String,
    val DISCIP: String,
    val KOW: String,
    val AUD: String,
    val PREP: String?,
    val CLASS: String,
    val GROUPS: List<RetrofitGroup>
)

data class RetrofitGroup(
    val GROUP_P: String,
    val PRIM: String
)
