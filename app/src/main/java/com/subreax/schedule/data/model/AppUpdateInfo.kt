package com.subreax.schedule.data.model

data class AppUpdateInfo(
    val version: String,
    val changes: String,
    val downloadLink: String,
    val createdAt: Long
)
