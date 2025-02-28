package com.subreax.schedule.data.repository.update

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GitHubRetrofitService {
    @GET("repos/{owner}/{repo}/releases/latest")
    @Headers(
        "Accept: application/vnd.github+json",
        "X-GitHub-Api-Version: 2022-11-28"
    )
    suspend fun getLatestRelease(@Path("owner") owner: String, @Path("repo") repo: String): GithubRelease
}
