package com.subreax.schedule.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
fun context(): Context {
    LocalConfiguration.current
    return LocalContext.current
}
