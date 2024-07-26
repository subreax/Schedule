package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.TeacherNameEntity

@Dao
interface TeacherNameDao {
    @Query("SELECT * FROM teacher_name WHERE id = :id")
    suspend fun getEntryById(id: Int): TeacherNameEntity?

    @Query("SELECT * FROM teacher_name WHERE value = :name")
    suspend fun getEntryByName(name: String): TeacherNameEntity?

    @Insert
    suspend fun addName(name: TeacherNameEntity)
}