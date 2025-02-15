package com.subreax.schedule.ui.component.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester

@Composable
fun AutoFocusable(component: @Composable (FocusRequester) -> Unit) {
    val focusRequester = remember { FocusRequester() }

    component(focusRequester)

    LaunchedEffect(focusRequester) {
        runCatching { focusRequester.requestFocus() }
    }
}
