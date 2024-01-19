package com.subreax.schedule.ui.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ScheduleRepository
) : ViewModel() {
    data class SubjectDetails(
        val name: String,
        val type: SubjectType,
        val place: String,
        val date: String,
        val time: String,
        val teacher: String
    )

    var subject by mutableStateOf(SubjectDetails("", SubjectType.Lecture, "", "", "", ""))
        private set

    init {
        val subjectId = savedStateHandle.get<Int>("id")!!
        viewModelScope.launch {
            val subject1 = repository.findSubjectById(subjectId)
                ?: throw IllegalStateException("Subject with id $subjectId not found")

            subject = SubjectDetails(
                name = subject1.name,
                type = subject1.type,
                place = subject1.place,
                date = formatDate(subject1.timeRange.start),
                time = subject1.timeRange.toString(Calendar.getInstance()),
                teacher = subject1.teacherName?.full() ?: "Не указано"
            )
        }
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date)
    }
}