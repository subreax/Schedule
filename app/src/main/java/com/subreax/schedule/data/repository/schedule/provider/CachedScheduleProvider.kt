package com.subreax.schedule.data.repository.schedule.provider

import android.util.Log
import com.subreax.schedule.data.local.owner.LocalOwnerDataSource
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.local.subjectname.LocalSubjectNameDataSource
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.network.owner.networkType
import com.subreax.schedule.data.network.schedule.NetworkScheduleDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/** Provides a schedule that will be cached in a local database
 *  after receiving it from the network. Passed schedule owner should exist in the database */
class CachedScheduleProvider(
    private val owner: ScheduleOwner,
    private val localOwnerDataSource: LocalOwnerDataSource,
    private val localScheduleDataSource: LocalScheduleDataSource,
    private val networkScheduleDataSource: NetworkScheduleDataSource
) : ScheduleProvider {
    private var _cachedSubjects = listOf<Subject>()

    override fun getOwner() = owner

    override suspend fun getSubjects(): Resource<List<Subject>> {
        return withContext(Dispatchers.Default) {
            try {
                if (!isCachedScheduleAlive()) {
                    updateSchedule(owner)
                }

                val subjects = loadSubjects(owner, 0L)
                _cachedSubjects = subjects
                Resource.Success(subjects)
            }
            catch (ex: Exception) {
                ex.printStackTrace()
                val msg = if (ex is IOException) {
                    "Сервер ТулГУ недоступен"
                } else {
                    "Не удалось загрузить расписание: ${ex.message}"
                }
                val subjects = loadSubjects(owner, 0L)
                _cachedSubjects = subjects
                Resource.Failure(UiText.hardcoded(msg), subjects)
            }
        }
    }

    private suspend fun isCachedScheduleAlive(): Boolean {
        val expiresAt = getScheduleLastUpdateTime() + SCHEDULE_LIFE_TIME_MS
        val now = System.currentTimeMillis()
        return now < expiresAt
    }

    private suspend fun getScheduleLastUpdateTime(): Long {
        return withContext(Dispatchers.Default) {
            localOwnerDataSource.getScheduleLastUpdateTime(owner.networkId)
        }
    }

    override suspend fun getSubjectById(id: Long): Subject? {
        return withContext(Dispatchers.Default) {
            localScheduleDataSource.findSubjectById(id)
        }
    }

    private suspend fun updateSchedule(owner: ScheduleOwner) {
        val hasCachedSchedule = localScheduleDataSource.hasSubjects(owner.networkId)
        val minSubjectEndTime = if (hasCachedSchedule) {
            System.currentTimeMillis()
        } else {
            0L
        }

        val subjects = networkScheduleDataSource.getSubjects(
            owner.networkId,
            owner.networkType,
            minSubjectEndTime
        )

        if (subjects.isNotEmpty()) {
            localScheduleDataSource.deleteSubjectsAfterSpecifiedTime(owner.networkId, minSubjectEndTime)
            localScheduleDataSource.insertSubjects(owner.networkId, subjects)
            localOwnerDataSource.setScheduleLastUpdateTime(owner.networkId, System.currentTimeMillis())
        } else {
            Log.w("ScheduleRepositoryImpl", "Schedule for ${owner.networkId} is empty")
        }
    }

    private suspend fun loadSubjects(owner: ScheduleOwner, minSubjectEndTime: Long): List<Subject> {
        // todo
        val subjects = (localScheduleDataSource.loadSchedule(owner.networkId, minSubjectEndTime)
                as Resource.Success).value

        return subjects
    }

    companion object {
        private const val SCHEDULE_LIFE_TIME_MS = 1000L * 60 * 30
    }
}