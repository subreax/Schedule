package com.subreax.schedule.ui

import com.subreax.schedule.data.model.SubjectType

fun SubjectType.stringValue(): String {
    return when (this) {
        SubjectType.Lecture -> "Лекция"
        SubjectType.Practice -> "Практика"
        SubjectType.Lab -> "Лабораторка"
        SubjectType.Test -> "Зачёт"
        SubjectType.DiffTest -> "Дифф. зачёт"
        SubjectType.Exam -> "Экзамен"
        SubjectType.Consult -> "Консультация"
        else -> "Неизвестный тип '$this'"
    }
}
