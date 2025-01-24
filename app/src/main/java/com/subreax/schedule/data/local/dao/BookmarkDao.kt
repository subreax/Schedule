package com.subreax.schedule.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.subreax.schedule.data.local.entitiy.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY position")
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

    @Query("UPDATE bookmarks SET position = :position WHERE id = :id")
    suspend fun updateBookmarkPosition(id: Int, position: Int)

    @Transaction
    suspend fun swapBookmarkPositions(pos1: Int, pos2: Int) {
        val bookmarks = getBookmarks().first()
        updateBookmarkPosition(bookmarks[pos1].id, pos2)
        updateBookmarkPosition(bookmarks[pos2].id, pos1)
    }

    @Transaction
    suspend fun repairBookmarkPositions() {
        val bookmarks = getBookmarks().first()
        if (bookmarks.isEmpty()) {
            return
        }

        val sum = bookmarks.sumOf { it.position + 1 }
        // 1 + 2 + 3 + ... + n = n * (n + 1) / 2
        val expectedSum = (bookmarks.size) * (bookmarks.size + 1) / 2
        if (sum != expectedSum) {
            bookmarks.forEachIndexed { idx, it ->
                updateBookmarkPosition(it.id, idx)
            }
        }
    }
}