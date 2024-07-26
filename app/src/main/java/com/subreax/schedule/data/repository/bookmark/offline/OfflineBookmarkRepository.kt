package com.subreax.schedule.data.repository.bookmark.offline

import com.subreax.schedule.data.local.dao.BookmarkDao
import com.subreax.schedule.data.local.entitiy.BookmarkEntity
import com.subreax.schedule.data.local.entitiy.asExternalModel
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
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
    private val bookmarkDao: BookmarkDao,
    private val scheduleIdRepository: ScheduleIdRepository
) : BookmarkRepository {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _bookmarks = bookmarkDao.getBookmarks()
        .map { list ->
            list.map { it.asExternalModel() }
        }
        .stateIn(coroutineScope, SharingStarted.Lazily, emptyList())

    override val bookmarks: Flow<List<ScheduleBookmark>>
        get() = _bookmarks

    override suspend fun addBookmark(scheduleId: String): Resource<ScheduleBookmark> {
        return coroutineScope.async {
            if (bookmarkDao.isBookmarkExist(scheduleId)) {
                return@async Resource.Failure(UiText.hardcoded("Закладка уже существует"))
            }

            val typeRes = getScheduleType(scheduleId)
            if (typeRes is Resource.Failure) {
                return@async Resource.Failure(typeRes.message)
            }

            val bookmark = BookmarkEntity(
                id = 0,
                scheduleId = scheduleId,
                type = typeRes.requireValue(),
                name = ScheduleBookmark.NO_NAME
            )
            bookmarkDao.addBookmark(bookmark)
            Resource.Success(bookmarkDao.findBookmark(scheduleId)!!.asExternalModel())
        }.await()
    }

    override suspend fun deleteBookmark(scheduleId: String): Resource<Unit> {
        return coroutineScope.async {
            if (bookmarkDao.isBookmarkExist(scheduleId)) {
                bookmarkDao.deleteBookmark(scheduleId)
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.hardcoded("Закладка не существует"))
            }
        }.await()
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

    override suspend fun isNotEmpty(): Boolean {
        return withContext(Dispatchers.IO) {
            bookmarkDao.isNotEmpty()
        }
    }

    override suspend fun setBookmarkName(scheduleId: String, name: String): Resource<Unit> {
        return coroutineScope.async {
            if (bookmarkDao.isBookmarkExist(scheduleId)) {
                bookmarkDao.updateBookmarkName(scheduleId, name)
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.hardcoded("Закладка не существует"))
            }
        }.await()
    }

    private suspend fun getScheduleType(scheduleId: String): Resource<ScheduleType> {
        return scheduleIdRepository.getScheduleId(scheduleId).ifSuccess {
            Resource.Success(it.type)
        }
    }
}