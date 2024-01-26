package com.subreax.schedule.data.network

import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.network.model.NetworkSubject

interface NetworkDataSource {
    suspend fun getOwnerType(owner: String): String?
    suspend fun getSubjects(owner: String, type: String): List<NetworkSubject>
    suspend fun isScheduleOwnerExists(scheduleOwner: String): Boolean
    suspend fun getScheduleOwnerHints(scheduleOwner: String): List<String>
}

val ScheduleOwner.networkType: String
    get() = when (type) {
        ScheduleOwner.Type.Student -> "GROUP_P"
        ScheduleOwner.Type.Teacher -> "PREP"
        ScheduleOwner.Type.Room -> "AUD"
    }

fun String.toScheduleOwnerType(): ScheduleOwner.Type {
    return when (this) {
        "GROUP_P" -> ScheduleOwner.Type.Student
        "PREP" -> ScheduleOwner.Type.Teacher
        "AUD" -> ScheduleOwner.Type.Room
        else -> error("Неизвестный тип: '$this'")
    }
}
