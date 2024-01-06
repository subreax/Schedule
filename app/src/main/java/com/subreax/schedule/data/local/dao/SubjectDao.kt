package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.local.entitiy.LocalSubject

@Dao
interface SubjectDao {
    @Query(
        "SELECT subject.id, subject.typeId, subject_name.alias AS name, subject.place, subject.teacherName AS teacher, subject.beginTimeMins, subject.endTimeMins, subject.note FROM subject " +
                "INNER JOIN subject_name ON subject_name.id = subject.nameId " +
                "WHERE subject.ownerId = :ownerId"
    )
    suspend fun findSubjectsByOwnerId(ownerId: Int): List<LocalExpandedSubject>

    @Insert
    suspend fun insert(data: List<LocalSubject>)

    @Query("DELETE FROM subject WHERE ownerId = :ownerId AND endTimeMins >= :endTime")
    suspend fun deleteSubjects(ownerId: Int, endTime: Long)

    @Query(
        "SELECT subject.id, subject.typeId, subject_name.alias AS name, subject.place, subject.teacherName AS teacher, subject.beginTimeMins, subject.endTimeMins, subject.note FROM subject " +
                "INNER JOIN subject_name ON subject_name.id = subject.nameId " +
                "WHERE subject.id = :id"
    )
    suspend fun findSubjectById(id: Int): LocalExpandedSubject?
}