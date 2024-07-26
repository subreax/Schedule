package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.SubjectNameEntity

@Dao
interface SubjectNameDao {
    @Query("SELECT * FROM subject_name WHERE id = :id")
    suspend fun getEntryById(id: Int): SubjectNameEntity?

    @Query("SELECT * FROM subject_name WHERE value = :name")
    suspend fun getEntryByName(name: String): SubjectNameEntity?

    @Insert
    suspend fun addEntry(name: SubjectNameEntity)

    @Query("UPDATE subject_name SET alias = :alias WHERE value = :name")
    suspend fun setNameAlias(name: String, alias: String)
}