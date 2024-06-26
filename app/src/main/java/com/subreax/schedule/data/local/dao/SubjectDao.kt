package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.local.entitiy.LocalSubject

@Dao
interface SubjectDao {
    @Query(
        "SELECT subject.id, " +
                "subject.typeId, " +
                "subject_name.value AS name," +
                "subject_name.alias AS nameAlias, " +
                "subject.place, " +
                "teacher_name.value AS teacher, " +
                "subject.beginTimeMins, " +
                "subject.endTimeMins, " +
                "subject.rawGroups " +
                "FROM subject " +
                "INNER JOIN subject_name ON subject_name.id = subject.subjectNameId " +
                "INNER JOIN teacher_name ON teacher_name.id = subject.teacherNameId " +
                "WHERE subject.ownerId = :ownerId " +
                "ORDER BY subject.beginTimeMins"
    )
    suspend fun findSubjectsByOwnerId(ownerId: Int): List<LocalExpandedSubject>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: List<LocalSubject>)

    @Query("DELETE FROM subject WHERE ownerId = :ownerId")
    suspend fun deleteSubjects(ownerId: Int)

    @Query("DELETE FROM subject WHERE ownerId = :ownerId AND endTimeMins >= :timeMins")
    suspend fun deleteSubjectsAfterSpecifiedTime(ownerId: Int, timeMins: Int)

    @Query(
        "SELECT subject.id, " +
                "subject.typeId, " +
                "subject_name.value AS name," +
                "subject_name.alias AS nameAlias, " +
                "subject.place, " +
                "teacher_name.value AS teacher," +
                "subject.beginTimeMins, " +
                "subject.endTimeMins, " +
                "subject.rawGroups " +
                "FROM subject " +
                "INNER JOIN subject_name ON subject_name.id = subject.subjectNameId " +
                "INNER JOIN teacher_name ON teacher_name.id = subject.teacherNameId " +
                "WHERE subject.id = :id"
    )
    suspend fun findSubjectById(id: Long): LocalExpandedSubject?

    @Query("SELECT COUNT(id) FROM subject WHERE ownerId = :ownerId")
    suspend fun countSubjects(ownerId: Int): Int

    @Query("SELECT subjectNameId FROM subject WHERE id = :subjectId")
    suspend fun getSubjectNameId(subjectId: Long): Int?
}