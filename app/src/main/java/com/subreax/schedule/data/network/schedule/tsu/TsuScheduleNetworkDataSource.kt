package com.subreax.schedule.data.network.schedule.tsu

import android.icu.util.Calendar
import android.util.Log
import com.subreax.schedule.R
import com.subreax.schedule.data.local.cache.LocalCache
import com.subreax.schedule.data.model.AcademicScheduleItem
import com.subreax.schedule.data.model.transformType
import com.subreax.schedule.data.network.TsuRetrofitService
import com.subreax.schedule.data.network.model.NetworkGroup
import com.subreax.schedule.data.network.model.NetworkSchedule
import com.subreax.schedule.data.network.model.NetworkScheduleType
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.model.RetrofitCalendarItem
import com.subreax.schedule.data.network.model.RetrofitSubject
import com.subreax.schedule.data.network.schedule.ScheduleNetworkDataSource
import com.subreax.schedule.data.repository.analytics.AnalyticsRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.IOException
import java.net.UnknownHostException
import java.util.Date
import kotlin.coroutines.cancellation.CancellationException

class TsuScheduleNetworkDataSource(
    private val localCache: LocalCache,
    private val service: TsuRetrofitService,
    private val analyticsRepository: AnalyticsRepository,
    private val ioDispatcher: CoroutineDispatcher
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
                if (retrofitSubjects.isEmpty()) {
                    return@withContext Resource.Failure(UiText.res(R.string.there_is_no_schedule_on_server))
                }

                val subjects = mutableListOf<NetworkSubject>()
                val calendar = Calendar.getInstance()
                retrofitSubjects.forEach {
                    ensureActive()
                    val timeRange = DateTimeUtils.parseTimeRange(it.DATE_Z, it.TIME_Z, calendar)
                    if (timeRange.end >= from) {
                        subjects.add(NetworkSubject(
                            name = it.transformSubjectName(),
                            place = it.AUD,
                            beginTime = timeRange.begin,
                            endTime = timeRange.end,
                            teacher = it.PREP,
                            type = it.transformType(),
                            groups = it.GROUPS.map { gr -> NetworkGroup(gr.GROUP_P, gr.PRIM) }
                        ))
                    }
                }
                ensureActive()

                val type = rawType.toNetworkScheduleType()
                if (type == NetworkScheduleType.Unknown) {
                    Log.e(TAG, "Unknown schedule type: $rawType")
                    analyticsRepository.recordException(
                        Exception("Unknown schedule type: $rawType"),
                        keys = mapOf("scheduleId" to id)
                    )
                }

                Resource.Success(NetworkSchedule(id, type, subjects))
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: IOException) {
                if (ex !is UnknownHostException) {
                    analyticsRepository.recordException(ex)
                }
                Resource.Failure(UiText.res(R.string.failed_to_fetch_schedule))
            } catch (ex: Exception) {
                analyticsRepository.recordException(ex)
                Resource.Failure(
                    UiText.res(
                        R.string.failed_to_process_schedule_s,
                        arrayOf(ex.message ?: "??")
                    )
                )
            }
        }
    }

    override suspend fun getAcademicSchedule(id: String): Resource<List<AcademicScheduleItem>> {
        return withContext(ioDispatcher) {
            try {
                val acSchedule = service.getCalendar(id).map { it.toModel() }
                Resource.Success(acSchedule)
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: IOException) {
                if (ex !is UnknownHostException) {
                    analyticsRepository.recordException(ex)
                }
                Resource.Failure(UiText.res(R.string.failed_to_get_academic_schedule_from_server))
            } catch (ex: Exception) {
                analyticsRepository.recordException(ex)
                Resource.Failure(
                    UiText.res(
                        R.string.failed_to_parse_academic_schedule_s,
                        ex.message ?: "unknown error"
                    )
                )
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
            else -> NetworkScheduleType.Unknown
        }
    }

    private fun RetrofitCalendarItem.toModel(): AcademicScheduleItem {
        return AcademicScheduleItem(
            title = title.replaceFirstChar { it.uppercaseChar() },
            begin = DateTimeUtils.parseDate(begin),
            end = Date(DateTimeUtils.parseDate(end).time + ONE_DAY_MS - 1000)
        )
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
            Resource.Failure(UiText.res(R.string.failed_to_get_schedule_type_s, scheduleId))
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
        private const val ONE_DAY_MS = 86400000L
    }
}

