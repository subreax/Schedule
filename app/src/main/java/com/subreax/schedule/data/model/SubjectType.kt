package com.subreax.schedule.data.model

import com.subreax.schedule.data.network.model.NetworkSubject

sealed class SubjectType(val ordinal: Int, val id: String, val name: String) {
    object Lecture : SubjectType(0, "lecture", "Лекция")
    object Practice : SubjectType(1, "practice", "Практика")
    object Lab : SubjectType(2, "lab", "Лаба")
    object Test : SubjectType(3, "test", "Зачёт")
    object DiffTest : SubjectType(4, "diffTest", "Дифф. зачёт")
    object Exam : SubjectType(5, "exam", "Экзамен")
    object Consult : SubjectType(6, "consult", "Консультация")
    class Unknown(name: String) : SubjectType(7, name, name)

    override fun toString() = name

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is SubjectType) return false
        return ordinal == other.ordinal
    }

    override fun hashCode(): Int {
        return ordinal
    }

    companion object {
        fun fromId(id: String): SubjectType {
            return subjectIdToInstanceMapping[id] ?: Unknown(id)
        }
    }
}


private val subjectIdToInstanceMapping = mapOf(
    SubjectType.Lecture.id to SubjectType.Lecture,
    SubjectType.Practice.id to SubjectType.Practice,
    SubjectType.Lab.id to SubjectType.Lab,
    SubjectType.Test.id to SubjectType.Test,
    SubjectType.DiffTest.id to SubjectType.DiffTest,
    SubjectType.Exam.id to SubjectType.Exam,
    SubjectType.Consult.id to SubjectType.Consult
)

fun NetworkSubject.transformType(): String {
    if (type == "default") {
        return when (kow) {
            "зч" -> SubjectType.Test.id
            "ДЗ" -> SubjectType.DiffTest.id
            "Экзамен" -> SubjectType.Exam.id
            "Консультации" -> SubjectType.Consult.id
            else -> kow
        }
    }
    return type
}

// todo: make theme-dependent
fun SubjectType.getArgbColor(): Long {
    return when (this) {
        SubjectType.Lecture -> 0xFF148175

        SubjectType.Practice -> 0xFF2FAB1B

        SubjectType.Lab -> 0xFFCF6B21

        SubjectType.Test,
        SubjectType.DiffTest,
        SubjectType.Exam,
        SubjectType.Consult -> 0xFFCA202E

        else -> 0xFFCC00CC
    }
}
