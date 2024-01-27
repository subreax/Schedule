package com.subreax.schedule.ui.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleOwner
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
        val ownerType: ScheduleOwner.Type,
        val name: String,
        val type: SubjectType,
        val place: String,
        val date: String,
        val time: String,
        val teacher: String,
        val groups: List<Group>,
        val note: String
    )

    var subject by mutableStateOf(
        SubjectDetails(
            ScheduleOwner.Type.Student,
            "",
            SubjectType.Lecture,
            "",
            "",
            "",
            "",
            emptyList(),
            ""
        )
    )
        private set

    init {
        val ownerTypeName = savedStateHandle.get<String>("owner_type_name")!!
        val ownerType = ScheduleOwner.Type.valueOf(ownerTypeName)
        val subjectId = savedStateHandle.get<Long>("subject_id")!!
        viewModelScope.launch {
            val subject1 = repository.findSubjectById(subjectId)
                ?: throw IllegalStateException("Subject with id $subjectId not found")

            val note = if (ownerType == ScheduleOwner.Type.Student)
                subject1.groups.first().note
            else
                ""

            val groups = if (ownerType != ScheduleOwner.Type.Student)
                subject1.groups
            else
                emptyList()

            subject = SubjectDetails(
                ownerType = ownerType,
                name = subject1.name,
                type = subject1.type,
                place = subject1.place,
                date = formatDate(subject1.timeRange.start),
                time = subject1.timeRange.toString(Calendar.getInstance()),
                teacher = subject1.teacher?.full() ?: "Не указано",
                groups = groups,
                note = note
            )
        }
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date)
    }
}