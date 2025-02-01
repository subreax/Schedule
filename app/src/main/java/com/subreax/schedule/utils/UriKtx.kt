package com.subreax.schedule.utils

import android.net.Uri
import androidx.core.net.toUri


fun geoUri(lat: Double, lon: Double): Uri {
    return "geo:$lat,$lon".toUri()
}

fun geoUri(query: String): Uri {
    return "geo:0,0?q=${query.urlEncode()}".toUri()
}
