package com.subreax.schedule.ui

import android.content.Context
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleItem
import com.subreax.schedule.ui.component.scheduleitemlist.toScheduleItems
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.approxBinarySearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class GetScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _schedule = MutableStateFlow(UiSchedule(nullScheduleId()))
    val schedule: Flow<UiSchedule> = _schedule

    private val _uiLoadingState = MutableStateFlow<UiLoadingState>(UiLoadingState.Loading)
    val uiLoadingState: StateFlow<UiLoadingState> = _uiLoadingState

    private var currentScheduleId = ""

    private var initJob: Job = Job()

    fun init(scheduleId: String) {
        if (currentScheduleId == scheduleId) {
            return
        }

        currentScheduleId = scheduleId
        refresh()
    }

    fun refresh() {
        coroutineScope.launch {
            _schedule.value = UiSchedule(nullScheduleId(currentScheduleId))
            _uiLoadingState.value = UiLoadingState.Loading

            initJob.cancel()
            initJob = coroutineScope.launch {
                when (val scheduleRes = scheduleRepository.getSchedule(currentScheduleId)) {
                    is Resource.Success -> {
                        _schedule.value = scheduleRes.value.toUiSchedule()
                        _uiLoadingState.value = UiLoadingState.Ready
                    }

                    is Resource.Failure -> {
                        _schedule.value = UiSchedule()
                        _uiLoadingState.value = UiLoadingState.Error(scheduleRes.message)
                    }
                }
            }
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
            id,
            items = items,
            updatedAt = syncTime,
            todayItemIndex = getTodayItemIndex(items)
        )
    }

    private fun Subject.toUiSubjectDetails(): UiSubjectDetails {
        val scheduleType = _schedule.value.id.type

        val groups = if (scheduleType == ScheduleType.Student) {
            emptyList()
        } else {
            this.groups
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
}

data class UiSchedule(
    val id: ScheduleId = nullScheduleId(),
    val items: List<ScheduleItem> = emptyList(),
    val updatedAt: Date = Date(),
    val todayItemIndex: Int = 0
)

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
    val groups: List<Group>,
    val note: String
)

private fun nullScheduleId(networkId: String = "") = ScheduleId(
    networkId, ScheduleType.Student
)
