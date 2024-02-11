package com.subreax.schedule.ui.scheduleexplorer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleItem
import com.subreax.schedule.ui.component.scheduleitemlist.ScheduleList
import com.subreax.schedule.ui.context
import com.subreax.schedule.ui.home.SubjectDetailsBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleExplorerScreen(
    viewModel: ScheduleExplorerViewModel,
    onOwnerIdClicked: (String) -> Unit,
    navBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val detailsSheet = rememberModalBottomSheetState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.navigationBars
    ) { paddings ->
        ScheduleExplorerScreen(
            ownerId = viewModel.ownerId,
            isLoading = viewModel.isLoading,
            items = viewModel.scheduleItems,
            onSubjectClicked = { item ->
                viewModel.openSubjectDetails(item.id)
            },
            navBack = navBack,
            modifier = Modifier.padding(paddings)
        )

        viewModel.pickedSubject?.let {
            SubjectDetailsBottomSheet(
                name = it.name,
                type = it.type,
                teacher = it.teacher,
                date = it.date,
                time = it.time,
                place = it.place,
                groups = it.groups,
                note = it.note,
                onIdClicked = { id ->
                    coroutineScope
                        .launch { detailsSheet.hide() }
                        .invokeOnCompletion { viewModel.hideSubjectDetails() }

                    onOwnerIdClicked(id)
                },
                onDismiss = {
                    coroutineScope
                        .launch { detailsSheet.hide() }
                        .invokeOnCompletion { viewModel.hideSubjectDetails() }
                },
                sheetState = detailsSheet
            )
        }
    }

    val context = context()
    LaunchedEffect(Unit) {
        viewModel.errors.collect {
            val msg = it.toString(context)
            snackbarHostState.showSnackbar(msg)
        }
    }
}

@Composable
fun ScheduleExplorerScreen(
    ownerId: String,
    isLoading: Boolean,
    items: List<ScheduleItem>,
    onSubjectClicked: (ScheduleItem.Subject) -> Unit,
    navBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
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

        ScheduleList(
            isLoading = isLoading,
            items = items,
            onSubjectClicked = onSubjectClicked,
            modifier = Modifier.fillMaxSize()
        )
    }
}
