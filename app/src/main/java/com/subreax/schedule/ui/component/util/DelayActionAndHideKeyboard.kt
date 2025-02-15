package com.subreax.schedule.ui.component.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import com.subreax.schedule.utils.WeakObjectHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias DialogAction = () -> Unit

@Composable
fun DelayActionAndHideKeyboard(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    delayMs: Long = 200,
    dialog: @Composable (delayAction: (DialogAction) -> Unit, installFocusManager: (FocusManager) -> Unit) -> Unit
) {
    val focusManagerHolder = remember { WeakObjectHolder<FocusManager>() }

    fun doAction(action: () -> Unit) {
        focusManagerHolder.value?.clearFocus()
        coroutineScope.launch {
            delay(delayMs)
            action()
        }
    }

    fun onFocusManagerAvailable(focusManager: FocusManager) {
        focusManagerHolder.value = focusManager
    }

    dialog(::doAction, ::onFocusManagerAvailable)
}
