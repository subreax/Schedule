package com.subreax.schedule.data.model

data class Schedule(
    val owner: ScheduleOwner,
    val subjects: List<Subject>,
    val type: Type = Type.Student // todo: currently unused
) {
    enum class Type {
        Student, Teacher, Room
    }
}
