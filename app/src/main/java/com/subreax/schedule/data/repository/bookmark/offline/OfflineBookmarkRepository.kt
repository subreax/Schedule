package com.subreax.schedule.data.repository.bookmark.offline

import com.subreax.schedule.data.local.dao.BookmarkDao
import com.subreax.schedule.data.local.entitiy.BookmarkEntity
import com.subreax.schedule.data.local.entitiy.asExternalModel
import com.subreax.schedule.data.model.ScheduleBookmark
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.repository.analytics.AnalyticsRepository
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import com.subreax.schedule.utils.ifFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineBookmarkRepository(
    private val bookmarkDao: BookmarkDao,
    private val scheduleIdRepository: ScheduleIdRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) : BookmarkRepository {
    private val _bookmarks = bookmarkDao.getBookmarks()
        .map { list ->
            list.map { it.asExternalModel() }
        }
        .stateIn(externalScope, SharingStarted.Lazily, emptyList())

    init {
        sendUserScheduleId()
    }

    override val bookmarks: Flow<List<ScheduleBookmark>>
        get() = _bookmarks

    override suspend fun addBookmark(
        scheduleId: String,
        name: String?,
        ignoreNotFound: Boolean
    ): Resource<ScheduleBookmark> {
        return externalScope.async {
            if (bookmarkDao.isBookmarkExist(scheduleId)) {
                return@async Resource.Failure(UiText.hardcoded("Закладка уже существует"))
            }

            val typeRes = getScheduleType(scheduleId)
            if (!ignoreNotFound && typeRes is Resource.Failure) {
                return@async Resource.Failure(typeRes.message)
            }

            val bookmark = BookmarkEntity(
                id = 0,
                scheduleId = scheduleId,
                type = typeRes.ifFailure { ScheduleType.Unknown },
                name = name ?: ScheduleBookmark.NO_NAME
            )
            bookmarkDao.addBookmark(bookmark)
            Resource.Success(bookmarkDao.findBookmark(scheduleId)!!.asExternalModel())
        }.await()
    }

    override suspend fun deleteBookmark(scheduleId: String): Resource<Unit> {
        return externalScope.async {
            if (bookmarkDao.isBookmarkExist(scheduleId)) {
                bookmarkDao.deleteBookmark(scheduleId)
                Resource.Success(Unit)
            } else {
                Resource.Failure(UiText.hardcoded("Закладка не существует"))
            }
        }.await()
    }

    override suspend fun getBookmark(scheduleId: String): Resource<ScheduleBookmark> {
        return withContext(ioDispatcher) {
            val bookmarkEntity = bookmarkDao.findBookmark(scheduleId)
            if (bookmarkEntity != null) {
                Resource.Success(bookmarkEntity.asExternalModel())
            } else {
                Resource.Failure(UiText.hardcoded("Not found"))
            }
        }
    }

    override suspend fun isNotEmpty(): Boolean {
        return withContext(ioDispatcher) {
            bookmarkDao.isNotEmpty()
        }
    }

    override suspend fun setBookmarkName(scheduleId: String, name: String): Resource<Unit> {
        return externalScope.async {
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

    private fun sendUserScheduleId() {
        externalScope.launch {
            val firstBookmark = bookmarkDao.getBookmarks()
                .first { it.isNotEmpty() }
                .first()

            analyticsRepository.sendUserScheduleId(firstBookmark.scheduleId)
        }
    }
}