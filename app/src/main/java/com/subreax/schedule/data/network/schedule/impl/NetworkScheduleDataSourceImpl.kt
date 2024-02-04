package com.subreax.schedule.data.network.schedule.impl

import android.util.Log
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.data.model.transformType
import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.network.model.RetrofitSubject
import com.subreax.schedule.data.network.owner.NetworkOwnerDataSource
import com.subreax.schedule.data.network.owner.toScheduleOwnerType
import com.subreax.schedule.data.network.schedule.NetworkScheduleDataSource
import com.subreax.schedule.utils.Resource
import javax.inject.Inject

class NetworkScheduleDataSourceImpl @Inject constructor(
    private val service: RetrofitService,
    private val networkOwnerDataSource: NetworkOwnerDataSource
) : NetworkScheduleDataSource {
    override suspend fun getSubjects(owner: String, type: String, minEndTime: Long): List<Subject> {
        val retrofitSubjects = service.getSubjects(owner, type)

        val result = mutableListOf<Subject>()
        retrofitSubjects.forEach {
            val timeRange = DateTimeUtils.parseTimeRange(it.DATE_Z, it.TIME_Z)
            if (timeRange.end.time >= minEndTime) {
                result.add(it.toModel(timeRange))
            }
        }
        return result
    }

    override suspend fun getSchedule(ownerId: String, minEndTime: Long): Schedule? {
        val typeRes = networkOwnerDataSource.getOwnerType(ownerId)
        if (typeRes is Resource.Failure) {
            return null // todo
        }
        val type = (typeRes as Resource.Success).value

        val owner = ScheduleOwner(ownerId, type.toScheduleOwnerType(), "")
        val subjects = getSubjects(ownerId, type, minEndTime)
        return Schedule(owner, subjects)
    }

    private fun RetrofitSubject.toModel(timeRange: TimeRange, id: Long = 0L): Subject {
        val teacher = PersonName.parse(PREP ?: "")

        return Subject(
            id = id,
            name = this.transformSubjectName(),
            place = AUD,
            timeRange = timeRange,
            teacher = teacher,
            type = SubjectType.fromId(transformType()),
            groups = GROUPS.map { Group(it.GROUP_P, it.PRIM) }
        )

    }

    private fun RetrofitSubject.transformSubjectName(): String {
        return try {
            if (DISCIP == "Иностранный язык") {
                transformSubjectNameAsForeignLang()
            } else if (DISCIP.startsWith("Физическая") && CLASS == "lecture") {
                "Спортивное сидение на лавках"
            } else {
                DISCIP
            }
        } catch (th: Throwable) {
            Log.e("NetworkDataSourceImpl", "Error occurred while parsing specific subject name", th)
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
}

