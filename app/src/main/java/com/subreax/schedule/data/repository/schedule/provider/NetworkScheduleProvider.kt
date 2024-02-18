package com.subreax.schedule.data.repository.schedule.provider

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.network.owner.networkType
import com.subreax.schedule.data.network.schedule.NetworkScheduleDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Provides a schedule */
class NetworkScheduleProvider(
    private val owner: ScheduleOwner,
    private val networkScheduleDataSource: NetworkScheduleDataSource
) : ScheduleProvider {
    private var _cachedSubjects = listOf<Subject>()

    override fun getOwner() = owner

    override suspend fun getSubjects(): Resource<List<Subject>> {
        return withContext(Dispatchers.Default) {
            try {
                val now = 0L
                val subjects = networkScheduleDataSource.getSubjects(owner.networkId, owner.networkType, now)
                    .sortedBy { it.timeRange.start }
                    .mapIndexed { index, subject ->
                        subject.copy(id = index.toLong())
                    }

                _cachedSubjects = subjects
                Resource.Success(subjects)
            } catch (ex: Exception) {
                _cachedSubjects = emptyList()
                val msg = "Не удалось загрузить расписание с сервера: ${ex.message}"
                Resource.Failure(UiText.hardcoded(msg), emptyList())
            }
        }
    }

    override suspend fun getSubjectById(id: Long): Subject? {
        val i = _cachedSubjects.binarySearch { it.id.compareTo(id) }
        return if (i >= 0) _cachedSubjects[i] else null
    }
}