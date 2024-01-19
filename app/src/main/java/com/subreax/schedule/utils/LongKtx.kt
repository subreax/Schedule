package com.subreax.schedule.utils

fun Long.toMinutes(): Int {
    return (this / 60000L).toInt()
}

fun Long.toMilliseconds(): Long {
    return this * 60000L
}
