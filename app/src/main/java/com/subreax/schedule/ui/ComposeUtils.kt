package com.subreax.schedule.ui

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.subreax.schedule.utils.TimeFormatter
import java.util.Date

@Composable
fun context(): Context {
    LocalConfiguration.current
    return LocalContext.current
}

@Composable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@Composable
fun formatTimeRelative(time: Date, now: Date = Date()): String {
    val res = resources()
    return TimeFormatter.formatRelative(res, time, now)
}
