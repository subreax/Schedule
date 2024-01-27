package com.subreax.schedule.ui.scheduleownermgr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.data.repository.scheduleowner.ScheduleOwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleOwnersManagerViewModel @Inject constructor(
    private val scheduleOwnerRepository: ScheduleOwnerRepository
) : ViewModel() {
    val owners = scheduleOwnerRepository.getOwners()

    var isDialogShown by mutableStateOf(false)
        private set

    private var dialogScheduleOwner: ScheduleOwner? = null
    var dialogName by mutableStateOf("")
        private set

    fun removeOwner(scheduleOwner: ScheduleOwner) {
        viewModelScope.launch {
            scheduleOwnerRepository.deleteOwner(scheduleOwner)
        }
    }

    fun showOwnerNameEditorDialog(owner: ScheduleOwner) {
        dialogScheduleOwner = owner
        dialogName = owner.name
        isDialogShown = true
    }

    fun ownerNameChanged(name: String) {
        dialogName = name
    }

    fun updateOwnerName() {
        viewModelScope.launch {
            scheduleOwnerRepository.updateOwnerName(dialogScheduleOwner!!.networkId, dialogName)
            dismissDialog()
        }
    }

    fun dismissDialog() {
        dialogScheduleOwner = null
        isDialogShown = false
    }
}
