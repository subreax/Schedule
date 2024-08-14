package com.subreax.schedule.ui.search_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.schedule_id.ScheduleIdRepository
import com.subreax.schedule.ui.SearchScheduleIdUseCase
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@HiltViewModel
class SearchScheduleViewModel @Inject constructor(
    scheduleIdRepository: ScheduleIdRepository
) : ViewModel() {
    private val searchScheduleIdUseCase = SearchScheduleIdUseCase(
        scheduleIdRepository,
        { errors.send(it) },
        viewModelScope
    )

    val searchId = searchScheduleIdUseCase.searchId
    val ids = searchScheduleIdUseCase.hints
    val isHintsLoading = searchScheduleIdUseCase.isLoading
    val errors = Channel<UiText>()

    fun updateSearchId(searchId: String) {
        searchScheduleIdUseCase.search(searchId)
    }
}