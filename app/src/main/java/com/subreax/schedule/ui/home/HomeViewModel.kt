package com.subreax.schedule.ui.home

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.schedule.ScheduleRepository
import com.subreax.schedule.utils.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {
    sealed class ScheduleItem {
        data class Subject(
            val name: String,
            val place: String,
            val timeRange: String,
            val teacherName: String,
            val type: SubjectType
        ) : ScheduleItem()

        data class Title(val title: String) : ScheduleItem()
    }

    var schedule = mutableStateListOf<ScheduleItem>()
        private set

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val repoSchedule = scheduleRepository.getScheduleForGroup("220431")
            val schedule1 = mutableListOf<ScheduleItem>()

            var oldSubjectDay = -1

            val calendar = Calendar.getInstance()

            repoSchedule.forEach {
                val subjectDay = getDayOfMonth(calendar, it.timeRange.start)

                if (oldSubjectDay != subjectDay) {
                    val title = DateFormatter.format(appContext, it.timeRange.start)
                    schedule1.add(ScheduleItem.Title(title))
                    oldSubjectDay = subjectDay
                }

                schedule1.add(
                    ScheduleItem.Subject(
                        name = it.name,
                        place = it.place,
                        timeRange = it.timeRange.toString(calendar),
                        teacherName = it.teacherName?.compact() ?: "",
                        type = it.type
                    )
                )
            }

            withContext(Dispatchers.Main) {
                schedule.addAll(schedule1)
            }
        }
    }

    private fun getDayOfMonth(calendar: Calendar, time: Date): Int {
        calendar.time = time
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}