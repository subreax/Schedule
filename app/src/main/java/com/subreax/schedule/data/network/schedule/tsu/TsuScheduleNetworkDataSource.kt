package com.subreax.schedule.data.network.schedule.tsu

import android.util.Log
import com.subreax.schedule.data.local.cache.LocalCache
import com.subreax.schedule.data.model.transformType
import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.network.model.NetworkGroup
import com.subreax.schedule.data.network.model.NetworkOwnerType
import com.subreax.schedule.data.network.model.NetworkSchedule
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.model.RetrofitSubject
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import java.util.Date
import javax.inject.Inject

class TsuScheduleNetworkDataSource @Inject constructor(
    private val localCache: LocalCache,
    private val service: RetrofitService
) : ScheduleNetworkDataSource {
    override suspend fun getSchedule(owner: String, minEndTime: Date): Resource<NetworkSchedule> {
        try {
            val ownerTypeRes = getOwnerType(owner)
            if (ownerTypeRes is Resource.Failure) {
                return Resource.Failure(ownerTypeRes.message)
            }

            val rawOwnerType = ownerTypeRes.requireValue()
            val ownerType = rawOwnerType.toNetworkOwnerType()
            val retrofitSubjects = service.getSubjects(owner, rawOwnerType)

            val subjects = mutableListOf<NetworkSubject>()
            retrofitSubjects.forEach {
                val timeRange = DateTimeUtils.parseTimeRange(it.DATE_Z, it.TIME_Z)
                if (timeRange.end >= minEndTime) {
                    subjects.add(NetworkSubject(
                        name = it.transformSubjectName(),
                        place = it.AUD,
                        beginTime = timeRange.start,
                        endTime = timeRange.end,
                        teacher = it.PREP,
                        type = it.transformType(),
                        groups = it.GROUPS.map { gr -> NetworkGroup(gr.GROUP_P, gr.PRIM) }
                    ))
                }
            }
            return Resource.Success(NetworkSchedule(owner, ownerType, subjects))
        } catch (ex: Exception) {
            return Resource.Failure(UiText.hardcoded("Не удалось загрузить расписание с сервера: ${ex.message}"))
        }
    }

    private fun RetrofitSubject.transformSubjectName(): String {
        return try {
            if (DISCIP == "Иностранный язык") {
                transformSubjectNameAsForeignLang()
            } else {
                DISCIP
            }
        } catch (th: Throwable) {
            th.printStackTrace()
            Log.e(TAG, "Error occurred while parsing specific subject name", th)
            DISCIP
        }
    }

    private fun RetrofitSubject.transformSubjectNameAsForeignLang(): String {
        val ob = KOW.lastIndexOf('(')
        return if (ob != -1) {
            val cb = KOW.indexOf(')', ob)
            val lang = KOW
                .substring(ob + 1, cb)
                .replaceFirstChar { it.uppercaseChar() }

            "$lang язык"
        } else {
            DISCIP
        }
    }

    private fun String.toNetworkOwnerType(): NetworkOwnerType {
        return when (this) {
            "GROUP_P" -> NetworkOwnerType.Student
            "PREP" -> NetworkOwnerType.Teacher
            "AUD" -> NetworkOwnerType.Room
            else -> {
                Log.e(TAG, "Unknown owner type: $this")
                NetworkOwnerType.Unknown
            }
        }
    }

    private suspend fun getOwnerType(owner: String): Resource<String> {
        val cachedType = getCachedOwnerType(owner)
        if (cachedType != null) {
            return Resource.Success(cachedType)
        }

        return fetchOwnerType(owner)
            .ifSuccess { type ->
                cacheOwnerType(owner, type)
                Resource.Success(type)
            }
    }

    private suspend fun fetchOwnerType(owner: String): Resource<String> {
        return try {
            val response = service.getDates(owner)
            if (response.error == null) {
                Resource.Success(response.scheduleType)
            } else {
                Resource.Failure(UiText.hardcoded(response.error))
            }
        } catch (ex: Exception) {
            Resource.Failure(UiText.hardcoded("Не удалось получить тип расписания '$owner'"))
        }
    }

    private suspend fun cacheOwnerType(owner: String, type: String) {
        localCache.set("TsuOwnerType/$owner", type)
    }

    private suspend fun getCachedOwnerType(owner: String): String? {
        return localCache.get("TsuOwnerType/$owner")
    }

    companion object {
        private const val TAG = "TsuScheduleNetworkDataSource"
    }
}

