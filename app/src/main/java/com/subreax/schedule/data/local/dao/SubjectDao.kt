package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.ExpandedSubjectEntity
import com.subreax.schedule.data.local.entitiy.SubjectEntity

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject WHERE ownerId = :scheduleId ORDER BY subject.beginTimeMins")
    suspend fun getSubjects(scheduleId: Int): List<ExpandedSubjectEntity>

    @Query("SELECT * FROM subject WHERE id = :id")
    suspend fun findSubjectById(id: Long): ExpandedSubjectEntity?

    @Insert
    suspend fun insertSubjects(data: List<SubjectEntity>)

    @Query("DELETE FROM subject WHERE ownerId = :scheduleId AND endTimeMins >= :fromMins")
    suspend fun deleteSubjects(scheduleId: Int, fromMins: Int)
}