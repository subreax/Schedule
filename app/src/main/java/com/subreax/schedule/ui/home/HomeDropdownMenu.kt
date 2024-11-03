package com.subreax.schedule.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

@Composable
fun HomeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    refreshSchedule: () -> Unit,
    navToAcademicSchedule: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = {
                Text("Обновить расписание")
            },
            leadingIcon = {
                Icon(Icons.Filled.Refresh, "Обновить расписание")
            },
            onClick = refreshSchedule
        )
        DropdownMenuItem(
            text = {
                Text("Учебный график")
            },
            leadingIcon = {
                Icon(Icons.Filled.CalendarMonth, "Учебный график")
            },
            onClick = navToAcademicSchedule
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
    refreshSchedule: () -> Unit,
    navToAcademicSchedule: () -> Unit
) {
    HomeDropdownMenu(
        expanded = state.isMenuShown.value,
        onDismissRequest = { state.hide() },
        refreshSchedule = {
            state.hide()
            refreshSchedule()
        },
        navToAcademicSchedule = {
            state.hide()
            navToAcademicSchedule()
        }
    )
}