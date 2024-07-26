package com.subreax.schedule.utils

fun Long.ms2min(): Int {
    return (this / 60000L).toInt()
}

fun Int.min2ms(): Long {
    return this * 60000L
}
