package com.subreax.schedule.data.network.owner

import com.subreax.schedule.data.model.ScheduleOwner

interface NetworkOwnerDataSource {
    suspend fun getOwnerHints(ownerId: String): List<String>
    suspend fun isOwnerExist(ownerId: String): Boolean
    suspend fun getOwnerType(ownerId: String): String?
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
