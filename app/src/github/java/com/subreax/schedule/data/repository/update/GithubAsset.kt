package com.subreax.schedule.data.repository.update

import com.google.gson.annotations.SerializedName

data class GithubAsset(
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String
)