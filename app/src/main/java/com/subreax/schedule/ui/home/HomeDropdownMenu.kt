package com.subreax.schedule.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.data.model.ScheduleType

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
    offset: DpOffset = DpOffset.Zero
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
        },
        offset = offset
    )
}

@Composable
fun HomeDropdownMenu(
    expanded: Boolean,
    showAcademicScheduleItem: Boolean,
    onDismissRequest: () -> Unit,
    refreshSchedule: () -> Unit,
    navToAcademicSchedule: () -> Unit,
    resetSchedule: () -> Unit,
    offset: DpOffset = DpOffset.Zero,
    shape: Shape = MaterialTheme.shapes.medium
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        shape = shape
    ) {
        CustomDropdownMenuItem(
            text = stringResource(R.string.update),
            leadingIcon = Icons.Filled.Refresh,
            onClick = refreshSchedule
        )

        if (showAcademicScheduleItem) {
            CustomDropdownMenuItem(
                text = stringResource(R.string.academic_schedule),
                leadingIcon = Icons.Filled.CalendarMonth,
                onClick = navToAcademicSchedule
            )
        }

        CustomDropdownMenuItem(
            text = stringResource(R.string.reset_history),
            leadingIcon = Icons.Outlined.DeleteForever,
            onClick = resetSchedule
        )
    }
}

@Composable
private fun CustomDropdownMenuItem(
    text: String,
    leadingIcon: ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(text)
        },
        onClick = onClick,
        leadingIcon = {
            Icon(leadingIcon, text)
        },
        contentPadding = PaddingValues(horizontal = 16.dp)
    )
}
