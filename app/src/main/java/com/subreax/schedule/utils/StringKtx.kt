package com.subreax.schedule.utils

fun String.join(vararg values: String): String {
    if (values.isEmpty()) {
        return ""
    }

    val builder = StringBuilder()
    values.forEach {
        if (it.isNotEmpty()) {
            builder.append(it).append(this)
        }
    }

    return builder.substring(0, builder.length - this.length)
}
