package com.subreax.schedule.ui

import android.content.Context
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleId
import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Settings
import com.subreax.schedule.data.repository.settings.SettingsRepository
import com.subreax.schedule.data.usecase.ScheduleUseCases
import com.subreax.schedule.ui.component.schedule.item.ScheduleItem
import com.subreax.schedule.ui.component.schedule.item.toScheduleItems
import com.subreax.schedule.utils.DateTimeUtils
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

enum class SyncType {
    None, IfNeeded, Force
}

private data class ScheduleSettings(
    val alwaysShowSubjectBeginTime: Boolean,
    val hideLectures: Boolean
)

class ScheduleContainer(
    private val scheduleUseCases: ScheduleUseCases,
    private val settingsRepository: SettingsRepository,
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val _schedule = MutableStateFlow(UiSchedule(nullScheduleId()))
    val schedule = _schedule.asStateFlow()

    private val _uiLoadingState = MutableStateFlow<UiLoadingState>(UiLoadingState.Loading)
    val loadingState = _uiLoadingState.asStateFlow()

    private var currentScheduleId = ""

    private var invokeJob: Job = Job()

    private val isScheduleReady: Boolean
        get() = invokeJob.isCompleted && _uiLoadingState.value == UiLoadingState.Ready

    private val shouldBeRefreshed: Boolean
        get() = isScheduleReady && areDaysDiffer(_schedule.value.syncTime, Date())

    private val scheduleSettings: StateFlow<ScheduleSettings>
        get() = settingsRepository.settings
            .map { it.toScheduleSettings() }
            .stateIn(coroutineScope, SharingStarted.Eagerly, Settings().toScheduleSettings())

    init {
        coroutineScope.launch {
            scheduleSettings.collect {
                if (isScheduleReady && currentScheduleId.isNotEmpty()) {
                    update(currentScheduleId, SyncType.None)
                }
            }
        }
    }

    fun update(id: String, syncType: SyncType = SyncType.IfNeeded): Job {
        currentScheduleId = id

        invokeJob.cancel()
        invokeJob = coroutineScope.launch {
            _uiLoadingState.value = UiLoadingState.Loading

            val res = when (syncType) {
                SyncType.None -> scheduleUseCases.get(id)
                SyncType.IfNeeded -> scheduleUseCases.syncIfNeededAndGet(id)
                SyncType.Force -> scheduleUseCases.syncAndGet(id)
            }

            val uiSchedule = res.toUiSchedule(scheduleSettings.value.alwaysShowSubjectBeginTime)
            ensureActive()

            _schedule.value = uiSchedule
            if (res is Resource.Success) {
                _uiLoadingState.value = UiLoadingState.Ready
            } else if (res is Resource.Failure) {
                _uiLoadingState.value = UiLoadingState.Error(res.message)
            }
        }
        return invokeJob
    }

    fun refreshIfNeeded() {
        coroutineScope.launch {
            if (invokeJob.isActive || !isScheduleReady) {
                return@launch
            }

            invokeJob.join()

            if (scheduleUseCases.isExpired(currentScheduleId)) {
                update(currentScheduleId, SyncType.Force)
            } else if (shouldBeRefreshed) {
                update(currentScheduleId, SyncType.None)
            }
        }
    }

    fun cancelSync() {
        if (invokeJob.isActive) {
            coroutineScope.launch {
                invokeJob.cancelAndJoin()
                update(currentScheduleId, SyncType.None)
            }
        }
    }

    fun resetSchedule() {
        coroutineScope.launch {
            cancelSync()
            scheduleUseCases.clear(currentScheduleId)
            update(currentScheduleId, SyncType.Force)
        }
    }

    private fun Resource<Schedule>.toUiSchedule(alwaysShowSubjectBeginTime: Boolean): UiSchedule {
        return if (this is Resource.Success) {
            value
        } else {
            (this as Resource.Failure).cachedValue
        }?.toUiSchedule(alwaysShowSubjectBeginTime) ?: UiSchedule()
    }

    private fun Schedule.toUiSchedule(alwaysShowSubjectBeginTime: Boolean): UiSchedule {
        val (items, todayItemIndex) = this.subjects.toScheduleItems(
            context,
            id.type,
            alwaysShowSubjectBeginTime
        )
        return UiSchedule(
            id = id,
            items = items,
            syncTime = syncTime,
            todayItemIndex = todayItemIndex.coerceAtLeast(0)
        )
    }

    private fun areDaysDiffer(t0: Date, t1: Date): Boolean {
        val date0 = DateTimeUtils.keepDateAndRemoveTime(t0.time)
        val date1 = DateTimeUtils.keepDateAndRemoveTime(t1.time)
        return date0 != date1
    }
}

data class UiSchedule(
    val id: ScheduleId = nullScheduleId(),
    val items: List<ScheduleItem> = emptyList(),
    val syncTime: Date = Date(),
    val todayItemIndex: Int = 0
)

private fun nullScheduleId(networkId: String = "") = ScheduleId(
    networkId, ScheduleType.Unknown
)

private fun Settings.toScheduleSettings(): ScheduleSettings {
    return ScheduleSettings(alwaysShowSubjectBeginTime, hideLectures)
}