package com.subreax.schedule.ui.ac_schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.LoadingContainer
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.ac_schedule.AcademicSchedule
import com.subreax.schedule.utils.toLocalizedString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AcademicScheduleScreen(
    navBack: () -> Unit,
    viewModel: AcademicScheduleViewModel = koinViewModel(),
) {
    val scheduleId = viewModel.scheduleId
    val acSchedule by viewModel.acSchedule.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val error = loadingState as? UiLoadingState.Error

    Column(Modifier.fillMaxSize()) {
        TopAppBarWithSubtitle(
            title = {
                Text(text = stringResource(R.string.academic_schedule))
            },
            subtitle = {
                Text(text = scheduleId)
            },
            navigationIcon = {
                IconButton(onClick = navBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.go_back))
                }
            }
        )

        LoadingContainer(
            isLoading = loadingState is UiLoadingState.Loading,
            modifier = Modifier.fillMaxSize()
        ) {
            AcademicSchedule(schedule = acSchedule, modifier = Modifier.fillMaxSize())
        }

        if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = error.message.toLocalizedString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}