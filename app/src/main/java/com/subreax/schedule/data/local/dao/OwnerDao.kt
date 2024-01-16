package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.LocalOwner
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owner")
    fun getOwners(): Flow<List<LocalOwner>>

    @Query("SELECT * FROM owner LIMIT 1")
    suspend fun getFirstOwner(): LocalOwner?

    @Query("SELECT * FROM owner WHERE networkId = :name")
    suspend fun findOwnerByNetworkId(name: String): LocalOwner?

    @Insert
    suspend fun addOwner(owner: LocalOwner)

    @Query("DELETE FROM owner WHERE networkId = :ownerName")
    suspend fun deleteOwnerByNetworkId(ownerName: String)

    @Query("UPDATE owner SET name = :name WHERE networkId = :networkId")
    suspend fun updateOwnerNameByNetworkId(networkId: String, name: String)
}