package com.subreax.schedule.utils

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

fun String.urlEncode(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}

fun String.urlDecode(): String {
    return URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
}