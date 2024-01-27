package com.subreax.schedule.data.model

data class ScheduleOwner(
    val networkId: String,
    val type: Type,
    val name: String
) {
    enum class Type {
        Student, Teacher, Room
    }
}
