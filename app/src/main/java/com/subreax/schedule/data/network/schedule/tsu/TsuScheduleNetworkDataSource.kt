package com.subreax.schedule.data.network.schedule.tsu

import android.util.Log
import com.subreax.schedule.data.local.cache.LocalCache
import com.subreax.schedule.data.model.transformType
import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.network.model.NetworkGroup
import com.subreax.schedule.data.network.model.NetworkScheduleType
import com.subreax.schedule.data.network.model.NetworkSchedule
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.model.RetrofitSubject
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.di.IODispatcher
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class TsuScheduleNetworkDataSource @Inject constructor(
    private val localCache: LocalCache,
    private val service: RetrofitService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ScheduleNetworkDataSource {
    override suspend fun getSchedule(id: String, from: Date): Resource<NetworkSchedule> {
        return withContext(ioDispatcher) {
            try {
                val typeRes = getScheduleType(id)
                if (typeRes is Resource.Failure) {
                    return@withContext Resource.Failure(typeRes.message)
                }

                val rawType = typeRes.requireValue()
                val retrofitSubjects = service.getSubjects(id, rawType)

                val subjects = mutableListOf<NetworkSubject>()
                retrofitSubjects.forEach {
                    ensureActive()
                    val timeRange = DateTimeUtils.parseTimeRange(it.DATE_Z, it.TIME_Z)
                    if (timeRange.end >= from) {
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
                val type = rawType.toNetworkScheduleType()
                Resource.Success(NetworkSchedule(id, type, subjects))
            }
            catch (ex: CancellationException) {
                throw ex
            }
            catch (ex: Exception) {
                Resource.Failure(UiText.hardcoded("Не удалось загрузить расписание с сервера: ${ex.message}"))
            }
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

    private fun String.toNetworkScheduleType(): NetworkScheduleType {
        return when (this) {
            "GROUP_P" -> NetworkScheduleType.Student
            "PREP" -> NetworkScheduleType.Teacher
            "AUD" -> NetworkScheduleType.Room
            else -> {
                Log.e(TAG, "Unknown schedule type: $this")
                NetworkScheduleType.Unknown
            }
        }
    }

    private suspend fun getScheduleType(scheduleId: String): Resource<String> {
        val cachedType = getCachedScheduleType(scheduleId)
        if (cachedType != null) {
            return Resource.Success(cachedType)
        }

        return fetchScheduleType(scheduleId)
            .ifSuccess { type ->
                cacheScheduleType(scheduleId, type)
                Resource.Success(type)
            }
    }

    private suspend fun fetchScheduleType(scheduleId: String): Resource<String> {
        return try {
            val response = service.getDates(scheduleId)
            if (response.error == null) {
                Resource.Success(response.scheduleType)
            } else {
                Resource.Failure(UiText.hardcoded(response.error))
            }
        } catch (ex: Exception) {
            Resource.Failure(UiText.hardcoded("Не удалось получить тип расписания '$scheduleId'"))
        }
    }

    private suspend fun cacheScheduleType(scheduleId: String, type: String) {
        localCache.set("TsuScheduleType/$scheduleId", type)
    }

    private suspend fun getCachedScheduleType(scheduleId: String): String? {
        return localCache.get("TsuScheduleType/$scheduleId")
    }

    companion object {
        private const val TAG = "TsuScheduleNetworkDataSource"
    }
}

