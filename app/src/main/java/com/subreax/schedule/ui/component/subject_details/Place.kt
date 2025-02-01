package com.subreax.schedule.ui.component.subject_details

import android.net.Uri
import com.subreax.schedule.utils.geoUri

data class Place(val value: String, val address: String?, val coordinates: MapCoordinates?)

fun Place.toUri(): Uri? {
    return if (address != null) {
        geoUri(address)
    } else if (coordinates != null) {
        geoUri(coordinates.lat, coordinates.lon)
    } else {
        null
    }
}