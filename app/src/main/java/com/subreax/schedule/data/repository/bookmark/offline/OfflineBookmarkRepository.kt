package com.subreax.schedule.data.repository.bookmark.offline

import com.subreax.schedule.data.local.dao.BookmarkDao
import com.subreax.schedule.data.local.entitiy.BookmarkEntity
import com.subreax.schedule.data.local.entitiy.asExternalModel
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineBookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _bookmarks = bookmarkDao.getBookmarks()
        .map { list ->
            list.map { it.asExternalModel() }
        }
        .stateIn(coroutineScope, SharingStarted.Lazily, emptyList())

    override val bookmarks: Flow<List<ScheduleBookmark>>
        get() = _bookmarks

    override suspend fun addBookmark(scheduleId: ScheduleId): Resource<ScheduleBookmark> {
        return coroutineScope.async {
            // todo: handle errors
            // todo: check for unique
            bookmarkDao.addBookmark(scheduleId.asNewBookmarkEntity())
            val bookmark = bookmarkDao.findBookmark(scheduleId.value)!!.asExternalModel()
            Resource.Success(bookmark)
        }.await()
    }

    override suspend fun deleteBookmark(scheduleId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            // todo: check if bookmark exist
            bookmarkDao.deleteBookmark(scheduleId)
            Resource.Success(Unit)
        }
    }

    override suspend fun getBookmark(scheduleId: String): Resource<ScheduleBookmark> {
        return withContext(Dispatchers.IO) {
            val bookmarkEntity = bookmarkDao.findBookmark(scheduleId)
            if (bookmarkEntity != null) {
                Resource.Success(bookmarkEntity.asExternalModel())
            } else {
                Resource.Failure(UiText.hardcoded("Not found"))
            }
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(Dispatchers.IO) {
            bookmarkDao.countBookmarks() == 0
        }
    }

    override suspend fun setBookmarkName(scheduleId: String, name: String): Resource<Unit> {
        // todo: handle errors
        return withContext(Dispatchers.IO) {
            bookmarkDao.updateBookmarkName(scheduleId, name)
            Resource.Success(Unit)
        }
    }

    private fun ScheduleId.asNewBookmarkEntity(
        name: String = ScheduleBookmark.NO_NAME
    ): BookmarkEntity {
        return BookmarkEntity(
            id = 0,
            scheduleId = value,
            typeValue = type.ordinal,
            name = name
        )
    }

    /*private fun BookmarkEntity.asExternalModel(): ScheduleBookmark {
        return ScheduleBookmark(
            scheduleId = ScheduleId(
                value = scheduleId,
                type = ScheduleType.entries[typeValue]
            ),
            name = name
        )
    }*/
}