package com.subreax.schedule.ui

import com.subreax.schedule.data.model.ScheduleType
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.repository.bookmark.BookmarkRepository
import com.subreax.schedule.data.usecase.SubjectUseCases
import com.subreax.schedule.ui.component.subject_details.GroupAndBookmark
import com.subreax.schedule.ui.component.subject_details.Place
import com.subreax.schedule.utils.ifFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class SubjectDetailsContainer(
    private val subjectUseCases: SubjectUseCases,
    private val bookmarkRepository: BookmarkRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val _subject = MutableStateFlow<UiSubjectDetails?>(null)
    val subject = _subject.asStateFlow()

    suspend fun show(id: Long, scheduleType: ScheduleType) {
        withContext(defaultDispatcher) {
            _subject.value = subjectUseCases.getById(id)?.toUiSubjectDetails(scheduleType)
        }
    }

    fun hide() {
        _subject.value = null
    }

    private suspend fun Subject.toUiSubjectDetails(scheduleType: ScheduleType): UiSubjectDetails {
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
            date = formatDate(timeRange.begin),
            time = timeRange.toString(Calendar.getInstance()),
            place = placeOf(place),
            groups = groups,
            note = note
        )
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(date)
    }

    private fun placeOf(value: String) = Place(value, subjectUseCases.getPlaceMapPoint(value))
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
    val place: Place,
    val groups: List<GroupAndBookmark>,
    val note: String
)
