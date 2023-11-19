package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.subreax.schedule.data.local.entitiy.LocalSubject

@Dao
interface SubjectDao {
    @Query("select * from subject where owner = :owner order by beginTime asc")
    suspend fun getLocalSubjectOrderedByBeginTime(owner: String): List<LocalSubject>

    @Upsert
    suspend fun upsert(schedule: List<LocalSubject>)

}