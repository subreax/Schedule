package com.subreax.schedule.data.repository.schedule_id.tsu

import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class TsuScheduleIdRepository @Inject constructor(
    private val service: RetrofitService
) : ScheduleIdRepository {
    override suspend fun getScheduleId(id: String): Resource<ScheduleId> {
        return handleExceptions {
            val dates = withContext(Dispatchers.IO) {
                service.getDates(id)
            }
            if (dates.error == null) {
                return@handleExceptions Resource.Success(
                    ScheduleId(
                        value = id,
                        type = parseScheduleType(dates.scheduleType)
                    )
                )
            }

            isScheduleIdExist(id).ifSuccess { exists ->
                if (exists) {
                    Resource.Success(
                        ScheduleId(
                            value = id,
                            type = ScheduleType.Unknown
                        )
                    )
                } else {
                    Resource.Failure(UiText.hardcoded("id расписания '$id' не существует"))
                }
            }
        }
    }

    override suspend fun getScheduleIds(startsWith: String): Resource<List<ScheduleId>> {
        return handleExceptions {
            withContext(Dispatchers.IO) {
                val ids = service.getDictionaries(startsWith)
                Resource.Success(ids.map {
                    ScheduleId(
                        value = it.value,
                        type = ScheduleType.Unknown
                    )
                })
            }
        }
    }

    override suspend fun isScheduleIdExist(id: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            when (val idsRes = getScheduleIds(id)) {
                is Resource.Success -> {
                    val firstId = idsRes.value.firstOrNull()?.value
                    Resource.Success(id == firstId)
                }

                is Resource.Failure -> {
                    Resource.Failure(idsRes.message)
                }
            }
        }
    }

    private fun parseScheduleType(raw: String): ScheduleType {
        return when (raw) {
            "GROUP_P" -> ScheduleType.Student
            "PREP" -> ScheduleType.Teacher
            "AUD" -> ScheduleType.Room
            else -> ScheduleType.Unknown
        }
    }

    private suspend fun <T> handleExceptions(block: suspend () -> Resource<T>): Resource<T> {
        return try {
            block()
        } catch (ex: IOException) {
            return Resource.Failure(UiText.hardcoded("Сетевая ошибка"))
        } catch (ex: Exception) {
            return Resource.Failure(UiText.hardcoded(ex.message ?: "Unknown error"))
        }
    }
}