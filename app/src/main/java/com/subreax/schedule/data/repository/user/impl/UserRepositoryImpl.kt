package com.subreax.schedule.data.repository.user.impl

import com.subreax.schedule.data.local.cache.LocalCache
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.user.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserRepositoryImpl(
    private val bookmarkRepository: BookmarkRepository,
    private val localCache: LocalCache,
    externalScope: CoroutineScope
) : UserRepository {
    private val _userType = MutableStateFlow(ScheduleType.Unknown)
    override val userType = _userType.asStateFlow()

    init {
        externalScope.launch {
            initUserType { _userType.value = it }
        }
    }

    private suspend fun initUserType(onInit: (ScheduleType) -> Unit) {
        val type = loadUserType()
        if (type == ScheduleType.Unknown) {
            val bookmarks = bookmarkRepository.bookmarks.first { it.isNotEmpty() }
            bookmarks.first().scheduleId.type.let {
                onInit(it)
                saveUserType(it)
            }
        } else {
            onInit(type)
        }
    }

    private suspend fun loadUserType(): ScheduleType {
        val type = localCache.get(USER_TYPE_KEY)
        return if (type != null) ScheduleType.valueOf(type) else ScheduleType.Unknown
    }

    private suspend fun saveUserType(userType: ScheduleType) {
        localCache.set(USER_TYPE_KEY, userType.toString())
    }

    companion object {
        private const val USER_TYPE_KEY = "UserRepositoryImpl/userType"
    }
}
