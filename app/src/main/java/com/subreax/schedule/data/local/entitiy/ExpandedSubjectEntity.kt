package com.subreax.schedule.data.local.entitiy

import androidx.room.Embedded
import androidx.room.Relation
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.utils.min2ms
import java.util.Date


data class ExpandedSubjectEntity(
    @Embedded val subject: SubjectEntity,
    @Relation(
        parentColumn = "subjectNameId",
        entityColumn = "id"
    )
    val subjectName: SubjectNameEntity,
    @Relation(
        parentColumn = "teacherNameId",
        entityColumn = "id"
    )
    val teacherName: TeacherNameEntity
)

fun ExpandedSubjectEntity.asExternalModel(): Subject {
    val teacher = if (teacherName.value.isNotEmpty())
        PersonName.parse(teacherName.value)
    else
        null

    return Subject(
        id = subject.id,
        name = subjectName.value,
        nameAlias = subjectName.alias,
        type = SubjectType.fromId(subject.typeId),
        place = subject.place,
        timeRange = TimeRange(
            Date(subject.beginTimeMins.min2ms()),
            Date(subject.endTimeMins.min2ms())
        ),
        groups = SubjectEntity.parseGroups(subject.rawGroups),
        teacher = teacher
    )
}