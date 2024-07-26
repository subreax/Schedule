package com.subreax.schedule.data.network.model

import com.google.gson.annotations.SerializedName

data class RetrofitDates(
    @SerializedName("MIN_DATE")
    val beginDate: String,

    @SerializedName("MAX_DATE")
    val endDate: String,

    @SerializedName("SEARCH_FIELD")
    val scheduleType: String,

    @SerializedName("error")
    val error: String?
)
