package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.SubjectEntity

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject WHERE subject.ownerId = :ownerId ORDER BY subject.beginTimeMins")
    suspend fun getSubjects(ownerId: Int): List<SubjectEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: List<SubjectEntity>)

    @Query("DELETE FROM subject WHERE ownerId = :ownerId")
    suspend fun deleteSubjects(ownerId: Int)

    @Query("DELETE FROM subject WHERE ownerId = :ownerId AND endTimeMins >= :timeMins")
    suspend fun deleteSubjectsAfterSpecifiedTime(ownerId: Int, timeMins: Int)

    @Query("SELECT * FROM subject WHERE id = :id")
    suspend fun findSubjectById(id: Long): SubjectEntity?

    @Query("SELECT COUNT(id) FROM subject WHERE ownerId = :ownerId")
    suspend fun countSubjects(ownerId: Int): Int

    @Query("SELECT subjectNameId FROM subject WHERE id = :subjectId")
    suspend fun getSubjectNameId(subjectId: Long): Int?
}