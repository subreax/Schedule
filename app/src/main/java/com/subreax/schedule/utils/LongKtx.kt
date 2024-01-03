package com.subreax.schedule.utils

fun Long.toMinutes(): Long {
    return this / 60000L
}

fun Long.toMilliseconds(): Long {
    return this * 60000L
}
