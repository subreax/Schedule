package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.subreax.schedule.data.local.entitiy.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks")
    fun getBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE scheduleId = :scheduleId")
    suspend fun findBookmark(scheduleId: String): BookmarkEntity?

    @Query("SELECT COUNT(1) from bookmarks WHERE scheduleId = :scheduleId")
    suspend fun isBookmarkExist(scheduleId: String): Boolean

    @Insert
    suspend fun addBookmark(entity: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE scheduleId = :scheduleId")
    suspend fun deleteBookmark(scheduleId: String)

    @Query("UPDATE bookmarks SET name = :name WHERE scheduleId = :scheduleId")
    suspend fun updateBookmarkName(scheduleId: String, name: String)

    @Query("SELECT COUNT(1) FROM bookmarks")
    suspend fun isNotEmpty(): Boolean
}