package com.subreax.schedule.ui.welcome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import com.subreax.schedule.ui.SearchScheduleIdUseCase
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterScheduleIdViewModel @Inject constructor(
    private val repository: ScheduleOwnerRepository
) : ViewModel() {
    private val searchScheduleIdUseCase = SearchScheduleIdUseCase(repository, viewModelScope)

    val searchId: StateFlow<String> = searchScheduleIdUseCase.searchId
    val suggestions = searchScheduleIdUseCase.suggestions

    var errorText: UiText? by mutableStateOf(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val _navHomeEvent = MutableSharedFlow<Unit>()
    val navHomeEvent: SharedFlow<Unit>
        get() = _navHomeEvent

    fun updateScheduleId(newValue: String) {
        errorText = null

        searchScheduleIdUseCase.search(newValue)
    }

    fun submit() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.addOwner(searchId.value)
            isLoading = false

            if (result is Resource.Failure) {
                errorText = result.message
            } else {
                _navHomeEvent.emit(Unit)
            }
        }
    }
}