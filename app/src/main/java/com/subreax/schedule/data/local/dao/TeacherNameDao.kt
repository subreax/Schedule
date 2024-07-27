package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.TeacherNameEntity

@Dao
interface TeacherNameDao {
    @Query("SELECT * FROM teacher_name WHERE value = :name")
    suspend fun getEntryByName(name: String): TeacherNameEntity?

    @Query("INSERT INTO teacher_name (value) VALUES (:name)")
    suspend fun addName(name: String)
}