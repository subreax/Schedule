package com.subreax.schedule.ui.component.schedule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class LoopHandle {
    var isRunning = true
        private set

    fun stop() {
        isRunning = false
    }
}

suspend inline fun CoroutineScope.runOnEachMinute(action: (LoopHandle) -> Unit) {
    val loop = LoopHandle()
    while (isActive) {
        action(loop)
        if (!loop.isRunning) {
            break
        }
        delayUntilNextMinute()
    }
}

suspend fun delayUntilNextMinute() {
    val now = System.currentTimeMillis()
    val msUntilNextMinute = 60000 - now % 60000
    delay(msUntilNextMinute)
}
