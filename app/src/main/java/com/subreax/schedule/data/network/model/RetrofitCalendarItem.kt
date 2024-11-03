package com.subreax.schedule.data.network.model

import com.google.gson.annotations.SerializedName

data class RetrofitCalendarItem(
    @SerializedName("VID")
    val title: String,
    @SerializedName("BEGIN_DATE")
    val begin: String,
    @SerializedName("END_DATE")
    val end: String
)