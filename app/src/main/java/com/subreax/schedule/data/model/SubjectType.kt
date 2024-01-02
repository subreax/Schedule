package com.subreax.schedule.data.model

sealed class SubjectType(val ordinal: Int, val value: String) {
    object Lecture : SubjectType(0, "Lecture")
    object Practice : SubjectType(1, "Practice")
    object Lab : SubjectType(2, "Lab")
    object Test : SubjectType(3, "Test")
    object DiffTest : SubjectType(4, "DiffTest")
    object Exam : SubjectType(5, "Exam")
    object Consult : SubjectType(6, "Consult")
    class Unknown(value: String) : SubjectType(7, value)

    override fun toString() = value

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is SubjectType) return false
        return ordinal == other.ordinal
    }

    override fun hashCode(): Int {
        var result = ordinal
        result = 31 * result + value.hashCode()
        return result
    }
}