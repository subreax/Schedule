package com.subreax.schedule.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import com.subreax.schedule.R
import com.subreax.schedule.data.model.ScheduleType

@Composable
fun HomeDropdownMenu(
    expanded: Boolean,
    showAcademicScheduleItem: Boolean,
    onDismissRequest: () -> Unit,
    refreshSchedule: () -> Unit,
    navToAcademicSchedule: () -> Unit,
    resetSchedule: () -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.update_schedule))
            },
            leadingIcon = {
                Icon(Icons.Filled.Refresh, stringResource(R.string.update_schedule))
            },
            onClick = refreshSchedule
        )

        if (showAcademicScheduleItem) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.academic_schedule))
                },
                leadingIcon = {
                    Icon(Icons.Filled.CalendarMonth, stringResource(R.string.academic_schedule))
                },
                onClick = navToAcademicSchedule
            )
        }

        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.reset_schedule_history))
            },
            leadingIcon = {
                Icon(Icons.Outlined.DeleteForever, stringResource(R.string.reset_schedule_history))
            },
            onClick = resetSchedule
        )
    }
}

class HomeDropdownMenuState(isMenuVisible: Boolean = false) {
    val isMenuShown = mutableStateOf(isMenuVisible)

    fun show() {
        isMenuShown.value = true
    }

    fun hide() {
        isMenuShown.value = false
    }
}

@Composable
fun HomeDropdownMenu(
    state: HomeDropdownMenuState,
    scheduleType: ScheduleType,
    refreshSchedule: () -> Unit,
    navToAcademicSchedule: () -> Unit,
    resetSchedule: () -> Unit,
) {
    HomeDropdownMenu(
        expanded = state.isMenuShown.value,
        showAcademicScheduleItem = scheduleType == ScheduleType.Student,
        onDismissRequest = { state.hide() },
        refreshSchedule = {
            state.hide()
            refreshSchedule()
        },
        navToAcademicSchedule = {
            state.hide()
            navToAcademicSchedule()
        },
        resetSchedule = {
            state.hide()
            resetSchedule()
        }
    )
}