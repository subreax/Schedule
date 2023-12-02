package com.subreax.schedule.data.network.model

import com.google.gson.annotations.SerializedName

data class RetrofitDates(
    @SerializedName("MIN_DATE")
    val dateStart: String,

    @SerializedName("MAX_DATE")
    val dateEnd: String,

    @SerializedName("SEARCH_FIELD")
    val idType: String,

    @SerializedName("error")
    val error: String?
)
