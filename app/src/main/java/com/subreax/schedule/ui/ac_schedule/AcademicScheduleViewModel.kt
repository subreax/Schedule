package com.subreax.schedule.ui.ac_schedule

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.AcademicScheduleItem
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.ac_schedule.UiAcademicScheduleItem
import com.subreax.schedule.utils.DateTimeUtils
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Date

class AcademicScheduleViewModel(
    savedStateHandle: SavedStateHandle,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    val scheduleId = savedStateHandle.get<String>("id")!!

    private val _acSchedule = MutableStateFlow<List<UiAcademicScheduleItem>>(emptyList())
    val acSchedule = _acSchedule.asStateFlow()

    private val _loadingState = MutableStateFlow<UiLoadingState>(UiLoadingState.Loading)
    val loadingState = _loadingState.asStateFlow()

    init {
        viewModelScope.launch {
            val res = scheduleRepository.getAcademicSchedule(scheduleId)
            when (res) {
                is Resource.Success -> {
                    val sdf = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
                    _acSchedule.value = res.value.map { it.toUiModel(sdf) }
                    _loadingState.value = UiLoadingState.Ready
                }

                is Resource.Failure -> {
                    _loadingState.value = UiLoadingState.Error(res.message)
                }
            }
        }
    }

    private fun AcademicScheduleItem.toUiModel(
        sdf: DateFormat,
        now: Long = System.currentTimeMillis(),
    ): UiAcademicScheduleItem {
        val beginStr = sdf.format(begin)
        val endStr = sdf.format(end)

        if (now < begin.time) {
            return UiAcademicScheduleItem.Pending(
                title = title,
                begin = beginStr,
                end = endStr,
                daysBeforeStart = DateTimeUtils.getDaysBetweenInclusive(Date(), begin)
            )
        } else if (now > end.time) {
            return UiAcademicScheduleItem.Finished(title, beginStr, endStr)
        } else {
            val remaining = 1f - (now - begin.time).toFloat() / (end.time - begin.time)
            return UiAcademicScheduleItem.Active(
                title = title,
                begin = beginStr,
                end = endStr,
                progress = remaining,
                daysRemaining = DateTimeUtils.getDaysBetweenInclusive(Date(), end)
            )
        }
    }
}
