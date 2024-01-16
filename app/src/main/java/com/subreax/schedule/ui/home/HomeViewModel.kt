package com.subreax.schedule.ui.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.Schedule
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.model.TimeRange
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.utils.DateFormatter
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val scheduleRepository: ScheduleRepository,
    private val scheduleOwnerRepository: ScheduleOwnerRepository
) : ViewModel() {
    sealed class ScheduleItem {
        data class Subject(
            val id: Int,
            val index: String,
            val name: String,
            val place: String,
            val timeRange: String,
            val teacherName: String,
            val type: SubjectType,
            val note: String?
        ) : ScheduleItem()

        data class Title(val title: String) : ScheduleItem()
    }

    var schedule = mutableStateListOf<ScheduleItem>()
        private set

    val scheduleOwners = scheduleOwnerRepository.getScheduleOwners()

    var currentScheduleOwner by mutableStateOf(ScheduleOwner(""))
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _errors = MutableSharedFlow<UiText>()
    val errors: SharedFlow<UiText>
        get() = _errors

    init {
        viewModelScope.launch {
            scheduleOwners
                .dropWhile { it.isEmpty() }
                .take(1)
                .collect {
                    getSchedule(it.first())
                }
        }
    }

    fun getSchedule(scheduleOwner: ScheduleOwner) {
        if (currentScheduleOwner.id != scheduleOwner.id) {
            viewModelScope.launch {
                currentScheduleOwner = scheduleOwner

                schedule.clear()
                isLoading = true
                val data = _getSchedule(scheduleOwner)
                isLoading = false
                schedule.addAll(data)
            }
        }
    }

    private suspend fun _getSchedule(owner: ScheduleOwner): List<ScheduleItem> = withContext(Dispatchers.Default) {
        val res = scheduleRepository.getSchedule(owner)
        var errorMessage: UiText? = null
        val schedule = when (res) {
            is Resource.Success -> {
                res.value
            }

            is Resource.Failure -> {
                errorMessage = res.message
                res.cachedValue ?: Schedule(owner, emptyList())
            }
        }

        schedule.toScheduleItems().also {
            errorMessage?.let {
                _errors.emit(it)
            }
        }
    }

    private fun Schedule.toScheduleItems(): List<ScheduleItem> {
        val items = mutableListOf<ScheduleItem>()
        val calendar = Calendar.getInstance()
        var oldSubjectDay = -1
        subjects.forEach {
            val subjectDay = getDayOfMonth(calendar, it.timeRange.start)

            if (oldSubjectDay != subjectDay) {
                val title = DateFormatter.format(appContext, it.timeRange.start)
                items.add(ScheduleItem.Title(title))
                oldSubjectDay = subjectDay
            }

            items.add(
                ScheduleItem.Subject(
                    id = it.id,
                    index = it.timeRange.getSubjectIndex(),
                    name = it.name,
                    place = it.place,
                    timeRange = it.timeRange.toString(calendar),
                    teacherName = it.teacherName?.compact() ?: "",
                    type = it.type,
                    note = it.note
                )
            )
        }

        return items
    }

    private fun TimeRange.getSubjectIndex(): String {
        val mins = ((start.time / 60000) % (60 * 24)).toInt()
        return when (mins) {
            gmt3MinutesOf(7, 45) -> "1"
            gmt3MinutesOf(9, 40) -> "2"
            gmt3MinutesOf(11, 35) -> "3"
            gmt3MinutesOf(13, 40) -> "4"
            gmt3MinutesOf(15, 35) -> "5"
            gmt3MinutesOf(17, 30) -> "6"
            else -> {
                val m = mins % 60
                val h = (3 + start.time / (1000*60*60)) % 24

                val mm = if (m >= 10) "$m" else "0$m"
                val hh = if (h >= 10) "$h" else "0$h"
                return "$hh\n$mm"
            }
        }
    }

    /** Returns total minutes of time since the beginning of the day.
        It should be passed in GMT+3 */
    private fun gmt3MinutesOf(hours: Int, mins: Int): Int {
        return (hours - 3) * 60 + mins
    }

    private fun getDayOfMonth(calendar: Calendar, time: Date): Int {
        calendar.time = time
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}