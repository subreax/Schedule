package com.subreax.schedule.data.model

import com.subreax.schedule.data.network.model.RetrofitSubject

sealed class SubjectType(val ordinal: Int, val id: String) {
    object Lecture : SubjectType(0, "lecture")
    object Practice : SubjectType(1, "practice")
    object Lab : SubjectType(2, "lab")
    object Test : SubjectType(3, "test")
    object DiffTest : SubjectType(4, "diffTest")
    object Exam : SubjectType(5, "exam")
    object Consult : SubjectType(6, "consult")
    object Coursework : SubjectType(7, "coursework")

    class Unknown(name: String) : SubjectType(8, name)

    override fun toString() = id

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is SubjectType) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return ordinal
    }

    companion object {
        fun fromId(id: String): SubjectType {
            return idToInstanceMapping[id] ?: Unknown(id)
        }
    }
}


private val idToInstanceMapping = mapOf(
    SubjectType.Lecture.id to SubjectType.Lecture,
    SubjectType.Practice.id to SubjectType.Practice,
    SubjectType.Lab.id to SubjectType.Lab,
    SubjectType.Test.id to SubjectType.Test,
    SubjectType.DiffTest.id to SubjectType.DiffTest,
    SubjectType.Exam.id to SubjectType.Exam,
    SubjectType.Consult.id to SubjectType.Consult,
    SubjectType.Coursework.id to SubjectType.Coursework
)

fun RetrofitSubject.transformType(): String {
    if (CLASS == "default") {
        return when (KOW) {
            "зч" -> SubjectType.Test.id
            "ДЗ" -> SubjectType.DiffTest.id
            "Экзамен" -> SubjectType.Exam.id
            "Э" -> SubjectType.Exam.id
            "Консультации" -> SubjectType.Consult.id
            "КР" -> SubjectType.Coursework.id
            else -> KOW
        }
    }
    return CLASS
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
        SubjectType.Consult,
        SubjectType.Coursework -> 0xFFCA202E

        else -> 0xFFCC00CC
    }
}
