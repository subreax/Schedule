package com.subreax.schedule.ui.scheduleexplorer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleItem
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleItemList

@Composable
fun ScheduleExplorerScreen(
    viewModel: ScheduleExplorerViewModel,
    ownerId: String,
    navToDetails: () -> Unit,
    navBack: () -> Unit,
) {
    val isLoading = viewModel.isLoading
    val items = viewModel.scheduleItems

    ScheduleExplorerScreen(
        ownerId = ownerId,
        isLoading = isLoading,
        items = items,
        onSubjectClicked = { item ->
            viewModel.openSubjectDetails(item.id)
        },
        navBack = navBack
    )

    LaunchedEffect(Unit) {
        viewModel.getSchedule(ownerId)

        viewModel.navToDetails.collect {
            navToDetails()
        }
    }
}

@Composable
fun ScheduleExplorerScreen(
    ownerId: String,
    isLoading: Boolean,
    items: List<ScheduleItem>,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    navBack: () -> Unit
) {
    Column {
        TopAppBarWithSubtitle(
            title = {
                Text("Просмотр расписания")
            },
            subtitle = {
                Text(text = ownerId)
            },
            navigationIcon = {
                IconButton(onClick = navBack) {
                    Icon(Icons.Filled.ArrowBack, "nav back")
                }
            }
        )

        ScheduleItemList(
            isLoading = isLoading,
            items = items,
            onSubjectClicked = onSubjectClicked,
            modifier = Modifier.fillMaxSize()
        )
    }
}
