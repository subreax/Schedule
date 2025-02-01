package com.subreax.schedule.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.subreax.schedule.R
import com.subreax.schedule.data.model.Settings
import com.subreax.schedule.ui.theme.ScheduleTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    navBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    SettingsScreen(
        settings = settings,
        onSettingsChanged = viewModel::updateSettings,
        navBack = navBack,
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: Settings,
    onSettingsChanged: (Settings) -> Unit,
    navBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Column {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.go_back))
                    }
                }
            )

            BooleanParam(
                name = stringResource(R.string.show_subject_begin_time_instead_of_its_number),
                value = settings.alwaysShowSubjectBeginTime,
                onValueChanged = {
                    onSettingsChanged(settings.copy(alwaysShowSubjectBeginTime = it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            EnumParam(
                name = stringResource(R.string.setting_app_theme),
                values = stringArrayResource(R.array.setting_app_theme_variants),
                selected = settings.appTheme.ordinal,
                onValueChanged = {
                    onSettingsChanged(
                        settings.copy(
                            appTheme = Settings.AppTheme.entries[it]
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            EnumParam(
                name = stringResource(R.string.setting_update_schedule),
                values = stringArrayResource(R.array.setting_update_schedule_variants),
                selected = when (settings.scheduleLifetimeMs) {
                    minutesOf(5) -> 0
                    minutesOf(30) -> 1
                    hoursOf(8) -> 2
                    hoursOf(24) -> 3
                    hoursOf(72) -> 4
                    else -> 5
                },
                onValueChanged = {
                    onSettingsChanged(
                        settings.copy(
                            scheduleLifetimeMs = when (it) {
                                0 -> minutesOf(5)
                                1 -> minutesOf(30)
                                2 -> hoursOf(8)
                                3 -> hoursOf(24)
                                4 -> hoursOf(72)
                                else -> hoursOf(24 * 365)
                            }
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun Param(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier.heightIn(64.dp), contentAlignment = Alignment.CenterStart) {
        content()
    }
}

@Composable
private fun BooleanParam(
    name: String,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Param(
        Modifier
            .clickable(onClick = { onValueChanged(!value) })
            .then(modifier)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name, modifier = Modifier.weight(1f))
            Switch(
                checked = value,
                onCheckedChange = onValueChanged,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }
}


@Composable
private fun EnumParam(
    name: String,
    values: Array<String>,
    selected: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Param(
        modifier = Modifier
            .clickable(onClick = { expanded = true })
            .then(modifier)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name, modifier = Modifier.weight(1f))
            Box {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    values.forEachIndexed { idx, text ->
                        DropdownMenuItem(
                            text = { Text(text) },
                            onClick = {
                                expanded = false
                                onValueChanged(idx)
                            }
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(values[selected])
                    Icon(Icons.Filled.ArrowDropDown, "")
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    ScheduleTheme {
        SettingsScreen(
            settings = Settings(),
            onSettingsChanged = { },
            navBack = {}
        )
    }
}

private fun minutesOf(mins: Int) = mins * 60000L
private fun hoursOf(hours: Int) = minutesOf(hours * 60)
