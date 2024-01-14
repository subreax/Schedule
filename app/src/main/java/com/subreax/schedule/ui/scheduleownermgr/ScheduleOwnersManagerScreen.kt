package com.subreax.schedule.ui.scheduleownermgr

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun ScheduleOwnersManagerScreen(
    viewModel: ScheduleOwnersManagerViewModel = hiltViewModel(),
    navToSchedulePicker: () -> Unit,
    navBack: () -> Unit
) {
    val owners by viewModel.owners.collectAsState()

    ScheduleOwnersManagerScreen(
        owners = owners,
        onAddClicked = navToSchedulePicker,
        onRemoveClicked = viewModel::removeOwner,
        navBack = navBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleOwnersManagerScreen(
    owners: List<ScheduleOwner>,
    onAddClicked: () -> Unit,
    onRemoveClicked: (ScheduleOwner) -> Unit,
    navBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Редактор", style = MaterialTheme.typography.titleMedium)
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                ),
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(Icons.Filled.ArrowBack, "Nav back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        ScheduleOwnerList(
            owners = owners,
            onItemClicked = {
                // todo
            },
            onRemoveClicked = onRemoveClicked,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ScheduleOwnerList(
    owners: List<ScheduleOwner>,
    onItemClicked: (ScheduleOwner) -> Unit,
    onRemoveClicked: (ScheduleOwner) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(owners) {
            ScheduleOwnerItem(
                id = it.id,
                alias = "Alias", // todo
                onClick = { onItemClicked(it) },
                onRemoveClicked = { onRemoveClicked(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ScheduleOwnerItem(
    id: String,
    alias: String,
    onClick: () -> Unit,
    onRemoveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = id, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemoveClicked) {
            Icon(Icons.Filled.Delete, "")
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScheduleOwnerManagerScreenPreview() {
    ScheduleTheme {
        Surface {
            ScheduleOwnersManagerScreen(
                owners = listOf(
                    ScheduleOwner("220431"),
                    ScheduleOwner("620221")
                ),
                onAddClicked = { },
                onRemoveClicked = { },
                navBack = { }
            )
        }
    }
}
