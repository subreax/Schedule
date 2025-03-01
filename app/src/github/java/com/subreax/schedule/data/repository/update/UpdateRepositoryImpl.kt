package com.subreax.schedule.data.repository.update

import android.util.Log
import com.subreax.schedule.data.model.AppUpdateInfo
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant


class UpdateRepositoryImpl : UpdateRepository {
    private val githubService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GitHubRetrofitService::class.java)
    }

    override suspend fun getLatestRelease(): Resource<AppUpdateInfo> {
        return withContext(Dispatchers.Default) {
            try {
                val release = githubService.getLatestRelease("subreax", "Schedule")
                val info = AppUpdateInfo(
                    version = release.tagName,
                    changes = release.body
                        .replace("\\r\\n", "\n")
                        .trim(),
                    downloadLink = release.assets.first().browserDownloadUrl,
                    createdAt = Instant.parse(release.publishedAt).toEpochMilli(),
                    //createdAt = System.currentTimeMillis()
                )
                Resource.Success(info)
            } catch (ex: Exception) {
                Log.e("UpdateRepositoryImpl", "Failed to get latest release", ex)
                Resource.Failure(UiText.hardcoded("Failed to get latest release. ${ex.message}"))
            }
        }
    }
}
