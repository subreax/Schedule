package com.subreax.schedule.ui

import android.content.Context
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.item.toScheduleItems
import com.subreax.schedule.ui.component.subject_details.GroupAndBookmark
import com.subreax.schedule.utils.DateTimeUtils
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.approxBinarySearch
import com.subreax.schedule.utils.ifFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class GetScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val _schedule = MutableStateFlow(UiSchedule(nullScheduleId()))
    val schedule = _schedule.asStateFlow()

    private val _uiLoadingState = MutableStateFlow<UiLoadingState>(UiLoadingState.Loading)
    val loadingState = _uiLoadingState.asStateFlow()

    private var currentScheduleId = ""

    private var initJob: Job = Job()

    private val isScheduleReady: Boolean
        get() = loadingState.value == UiLoadingState.Ready

    private val isScheduleExpired: Boolean
        get() = schedule.value.isExpired || areDaysDiffer(Date(), schedule.value.syncTime)


    fun init(scheduleId: String) {
        if (currentScheduleId == scheduleId) {
            return
        }

        currentScheduleId = scheduleId
        refresh()
    }

    fun refresh(invalidate: Boolean = false): Job {
        _uiLoadingState.value = UiLoadingState.Loading

        initJob.cancel()
        initJob = coroutineScope.launch(Dispatchers.Default) {
            when (val scheduleRes = scheduleRepository.getSchedule(
                currentScheduleId,
                invalidate = invalidate
            )) {
                is Resource.Success -> {
                    _schedule.value = scheduleRes.value.toUiSchedule()
                    _uiLoadingState.value = UiLoadingState.Ready
                }

                is Resource.Failure -> {
                    _schedule.value = scheduleRes.cachedValue?.toUiSchedule() ?: UiSchedule()
                    _uiLoadingState.value = UiLoadingState.Error(scheduleRes.message)
                }
            }
        }
        return initJob
    }

    fun refreshIfExpired(): Job? {
        return if (isScheduleReady && isScheduleExpired) {
            refresh()
        } else {
            null
        }
    }

    suspend fun getSubjectDetails(subjectId: Long): Resource<UiSubjectDetails> {
        return withContext(Dispatchers.Default) {
            val subjectRes = scheduleRepository.getSubjectById(subjectId)
            if (subjectRes is Resource.Success) {
                Resource.Success(subjectRes.value.toUiSubjectDetails())
            } else {
                Resource.Failure((subjectRes as Resource.Failure).message)
            }
        }
    }

    private fun Schedule.toUiSchedule(): UiSchedule {
        val items = this.subjects.toScheduleItems(context, id.type)
        return UiSchedule(
            id = id,
            items = items,
            syncTime = syncTime,
            expiresAt = expiresAt,
            todayItemIndex = getTodayItemIndex(items)
        )
    }

    private suspend fun Subject.toUiSubjectDetails(): UiSubjectDetails {
        val scheduleType = _schedule.value.id.type

        val groups = if (scheduleType == ScheduleType.Student) {
            emptyList()
        } else {
            this.groups.map {
                val bookmark = bookmarkRepository.getBookmark(it.id).ifFailure { null }
                GroupAndBookmark(it, bookmark)
            }
        }

        val note = if (scheduleType == ScheduleType.Student) {
            this.groups.firstOrNull()?.note ?: ""
        } else {
            ""
        }

        return UiSubjectDetails(
            subjectId = id,
            scheduleType = scheduleType,
            name = name,
            nameAlias = nameAlias,
            type = type,
            teacher = teacher?.full() ?: "",
            date = formatDate(timeRange.start),
            time = timeRange.toString(Calendar.getInstance()),
            place = place,
            groups = groups,
            note = note
        )
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date)
    }

    private fun getTodayItemIndex(items: List<ScheduleItem>): Int {
        if (items.isEmpty()) {
            return 0
        }

        val calendar = android.icu.util.Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val today = calendar.time
        val (left, right) = items.approxBinarySearch { it.date.compareTo(today) }
        return if (items[left] is ScheduleItem.Title) {
            left
        } else {
            right
        }
    }

    private fun areDaysDiffer(t0: Date, t1: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = t0
        val d0 = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.time = t1
        val d1 = calendar.get(Calendar.DAY_OF_MONTH)

        return d0 != d1
    }
}

data class UiSchedule(
    val id: ScheduleId = nullScheduleId(),
    val items: List<ScheduleItem> = emptyList(),
    val syncTime: Date = Date(),
    val expiresAt: Date = Date(System.currentTimeMillis() + DateTimeUtils.ONE_DAY_MS),
    val todayItemIndex: Int = 0
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt.time
}

data class UiSubjectDetails(
    val subjectId: Long,
    val scheduleType: ScheduleType,
    val name: String,
    val nameAlias: String,
    val type: SubjectType,
    val teacher: String,
    val date: String,
    val time: String,
    val place: String,
    val groups: List<GroupAndBookmark>,
    val note: String
)

private fun nullScheduleId(networkId: String = "") = ScheduleId(
    networkId, ScheduleType.Student
)
