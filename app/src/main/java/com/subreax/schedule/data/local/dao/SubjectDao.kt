package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.subreax.schedule.data.local.entitiy.ExpandedSubjectEntity
import com.subreax.schedule.data.local.entitiy.SubjectEntity
import java.util.Date

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subject WHERE ownerId = :scheduleId ORDER BY subject.beginTimeMins")
    suspend fun getSubjects(scheduleId: Int): List<ExpandedSubjectEntity>

    @Query("SELECT * FROM subject WHERE id = :id")
    suspend fun findSubjectById(id: Long): ExpandedSubjectEntity?

    @Insert
    suspend fun insertSubjects(data: List<SubjectEntity>)

    @Query("DELETE FROM subject WHERE ownerId = :scheduleId AND endTimeMins >= :fromMins")
    suspend fun deleteSubjectsInclusive(scheduleId: Int, fromMins: Int)

    @Transaction
    suspend fun replaceSubjects(
        localScheduleId: Int,
        subjects: List<SubjectEntity>,
        clearFrom: Date
    ) {
        val fromMins = ((clearFrom.time + 59999L) / 60000L).toInt()
        deleteSubjectsInclusive(localScheduleId, fromMins)
        insertSubjects(subjects)
    }
}