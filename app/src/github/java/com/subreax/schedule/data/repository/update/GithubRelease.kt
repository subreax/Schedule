package com.subreax.schedule.data.repository.update

import com.google.gson.annotations.SerializedName

data class GithubRelease(
    @SerializedName("tag_name")
    val tagName: String,
    val body: String,
    val assets: List<GithubAsset>,
    @SerializedName("published_at")
    val publishedAt: String
)

