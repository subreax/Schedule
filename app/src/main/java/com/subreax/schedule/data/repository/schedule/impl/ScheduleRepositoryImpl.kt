package com.subreax.schedule.data.repository.schedule.impl

import com.subreax.schedule.data.local.LocalDataSource
import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.data.model.transformType
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.toMilliseconds
import com.subreax.schedule.utils.toMinutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource
) : ScheduleRepository {
    // todo: add pagination?
    override suspend fun getSchedule(owner: String): Resource<List<Subject>> {
        return withContext(Dispatchers.Default) {
            try {
                val schedule = fetchScheduleForGroup(owner)
                saveSchedule(owner, schedule)
                Resource.Success(loadSchedule(owner))
            } catch (ex: Exception) {
                val msg = "Не удалось загрузить расписание с сервера: ${ex.message}"
                Resource.Failure(UiText.hardcoded(msg), loadSchedule(owner))
            }
        }
    }

    private suspend fun fetchScheduleForGroup(group: String): List<Subject> {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val networkSchedule = networkDataSource.getSchedule(group)
            networkSchedule
                .filter { it.endTime.time >= now }
                .map {
                    Subject(
                        id = 0,
                        name = it.name,
                        place = it.place,
                        timeRange = TimeRange(it.beginTime, it.endTime),
                        teacherName = it.teacher,
                        type = SubjectType.fromId(it.transformType()),
                    )
                }
                .sortedBy { it.timeRange.start.time }
        }
    }

    override suspend fun getLastRequestedScheduleOwner(): String? = withContext(Dispatchers.IO) {
        // todo
        localDataSource.getScheduleOwners().firstOrNull()?.id
    }

    private suspend fun saveSchedule(owner: String, schedule: List<Subject>) {
        localDataSource.saveSchedule(owner, schedule)
    }

    private suspend fun loadSchedule(owner: String): List<Subject> {
        return withContext(Dispatchers.IO) {
            val nowMinutes = System.currentTimeMillis().toMinutes()
            localDataSource.loadSchedule(owner)
                .filter { it.endTimeMins >= nowMinutes }
                .map { it.toModel() }
        }
    }

    override suspend fun findSubjectById(id: Int): Subject? = withContext(Dispatchers.IO) {
        localDataSource.findSubjectById(id)?.toModel()
    }

    private fun LocalExpandedSubject.toModel(): Subject {
        return Subject(
            id = id,
            name = name,
            place = place,
            type = SubjectType.fromId(typeId),
            timeRange = TimeRange(
                Date(beginTimeMins.toMilliseconds()),
                Date(endTimeMins.toMilliseconds())
            ),
            teacherName = if (teacher.isNotEmpty()) {
                PersonName.parse(teacher)
            } else {
                null
            }
        )
    }
}