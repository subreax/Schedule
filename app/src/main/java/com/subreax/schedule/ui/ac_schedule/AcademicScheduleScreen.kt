package com.subreax.schedule.ui.ac_schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.ui.component.NoDataPlaceholder
import com.subreax.schedule.ui.UiLoadingState
import com.subreax.schedule.ui.component.LoadingContainer
import com.subreax.schedule.ui.component.TopAppBarWithSubtitle
import com.subreax.schedule.ui.component.ac_schedule.AcademicScheduleList
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

    Column(Modifier.fillMaxSize().navigationBarsPadding()) {
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
            if (loadingState is UiLoadingState.Ready) {
                AcademicScheduleList(schedule = acSchedule, modifier = Modifier.fillMaxSize())
            } else {
                val errorMsg = (loadingState as UiLoadingState.Error).message.toLocalizedString()
                NoDataPlaceholder(
                    icon = Icons.Filled.Close,
                    text = errorMsg,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                )
            }
        }
    }
}