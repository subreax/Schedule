package com.subreax.schedule.data.repository.bookmark

import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    val bookmarks: Flow<List<ScheduleBookmark>>

    suspend fun addBookmark(scheduleId: String, name: String? = null): Resource<ScheduleBookmark>
    suspend fun getBookmark(scheduleId: String): Resource<ScheduleBookmark>
    suspend fun deleteBookmark(scheduleId: String): Resource<Unit>
    suspend fun isNotEmpty(): Boolean
    suspend fun setBookmarkName(scheduleId: String, name: String): Resource<Unit>
}