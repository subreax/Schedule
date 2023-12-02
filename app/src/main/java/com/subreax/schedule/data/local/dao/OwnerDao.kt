package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.LocalOwner

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owner")
    suspend fun getOwners(): List<LocalOwner>

    @Query("SELECT * FROM owner WHERE name = :name")
    suspend fun findOwnerByName(name: String): LocalOwner?

    @Insert
    suspend fun addOwner(owner: LocalOwner)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addOwnerIfNotExist(owner: LocalOwner)
}