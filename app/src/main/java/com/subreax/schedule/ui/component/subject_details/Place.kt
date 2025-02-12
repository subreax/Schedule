package com.subreax.schedule.ui.component.subject_details

import android.net.Uri
import com.subreax.schedule.data.usecase.subject.MapPoint
import com.subreax.schedule.utils.geoUri

data class Place(val value: String, val mapPoint: MapPoint?)

fun MapPoint.toUri(): Uri {
    return when (this) {
        is MapPoint.Address -> geoUri(address)
        is MapPoint.Coordinates -> geoUri(lat, lon)
    }
}