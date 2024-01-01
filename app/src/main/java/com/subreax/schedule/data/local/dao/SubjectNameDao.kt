package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.LocalSubjectName

@Dao
interface SubjectNameDao {
    @Query("SELECT id FROM subject_name WHERE value = :name")
    suspend fun getNameId(name: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNameIfNotExist(name: LocalSubjectName)
}