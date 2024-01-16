package com.subreax.schedule.data.repository.schedule.impl

import android.util.Log
import com.subreax.schedule.data.local.schedule.LocalScheduleDataSource
import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleOwner
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
    private val localScheduleDataSource: LocalScheduleDataSource
) : ScheduleRepository {
    // todo: add pagination?
    override suspend fun getSchedule(owner: ScheduleOwner): Resource<Schedule> {
        return withContext(Dispatchers.Default) {
            val minSubjectEndTime = System.currentTimeMillis()
            try {
                updateSchedule(owner, minSubjectEndTime)
                val schedule = loadSchedule(owner, minSubjectEndTime)
                Resource.Success(schedule)
            } catch (ex: Exception) {
                val msg = "Не удалось загрузить расписание с сервера: ${ex.message}"
                val schedule = loadSchedule(owner, minSubjectEndTime)
                Resource.Failure(UiText.hardcoded(msg), schedule)
            }
        }
    }

    private suspend fun updateSchedule(owner: ScheduleOwner, minSubjectEndTime: Long) {
        val schedule = fetchSchedule(owner = owner, minSubjectEndTime = minSubjectEndTime)
        if (schedule.subjects.isNotEmpty()) {
            localScheduleDataSource.updateSchedule(owner.id, schedule.subjects)
        } else {
            Log.w("ScheduleRepositoryImpl", "Schedule for ${owner.id} is empty")
        }
    }

    private suspend fun fetchSchedule(owner: ScheduleOwner, minSubjectEndTime: Long): Schedule {
        val networkSubjects = networkDataSource.getSubjects(owner.id)
        val subjects = networkSubjects
            .filter { minSubjectEndTime <= it.endTime.time }
            .map {
                Subject(
                    id = 0,
                    name = it.name,
                    place = it.place,
                    timeRange = TimeRange(it.beginTime, it.endTime),
                    teacherName = it.teacher,
                    type = SubjectType.fromId(it.transformType()),
                    note = it.note
                )
            }
            .sortedBy { it.timeRange.start.time }

        return Schedule(owner, subjects)
    }

    private suspend fun loadSchedule(owner: ScheduleOwner, minSubjectEndTime: Long): Schedule {
        // todo
        val localSubjects =
            (localScheduleDataSource.loadSchedule(owner.id) as Resource.Success).value

        val minEnd = minSubjectEndTime.toMinutes()
        val subjects = localSubjects
            .filter { minEnd <= it.endTimeMins }
            .map { it.toModel() }

        return Schedule(owner, subjects)
    }

    override suspend fun deleteSchedule(owner: ScheduleOwner): Resource<Unit> {
        return localScheduleDataSource.deleteSchedule(owner.id)
    }

    override suspend fun findSubjectById(id: Int): Subject? {
        return localScheduleDataSource.findSubjectById(id)?.toModel()
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
            },
            note = note.ifEmpty { null }
        )
    }
}