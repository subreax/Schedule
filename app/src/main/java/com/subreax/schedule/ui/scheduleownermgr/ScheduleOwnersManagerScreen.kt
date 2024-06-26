package com.subreax.schedule.ui.scheduleownermgr

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.subreax.schedule.ui.component.TextFieldDialog
import com.subreax.schedule.ui.theme.ScheduleTheme

@Composable
fun ScheduleOwnersManagerScreen(
    viewModel: ScheduleOwnersManagerViewModel = hiltViewModel(),
    navToSchedulePicker: () -> Unit,
    navBack: () -> Unit
) {
    val owners by viewModel.owners.collectAsState()
    val showEditNameDialog = viewModel.isDialogShown

    ScheduleOwnersManagerScreen(
        owners = owners,
        onAddClicked = navToSchedulePicker,
        onEditClicked = viewModel::showOwnerNameEditorDialog,
        onRemoveClicked = viewModel::removeOwner,
        navBack = navBack
    )

    if (showEditNameDialog) {
        TextFieldDialog(
            title = "Изменение имени",
            name = viewModel.dialogName,
            onNameChange = viewModel::ownerNameChanged,
            onSave = viewModel::updateOwnerName,
            onDismiss = viewModel::dismissDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleOwnersManagerScreen(
    owners: List<ScheduleOwner>,
    onAddClicked: () -> Unit,
    onEditClicked: (ScheduleOwner) -> Unit,
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
            FloatingActionButton(onClick = onAddClicked, shape = CircleShape) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        ScheduleOwnerList(
            owners = owners,
            onEditClicked = onEditClicked,
            onRemoveClicked = onRemoveClicked,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ScheduleOwnerList(
    owners: List<ScheduleOwner>,
    onEditClicked: (ScheduleOwner) -> Unit,
    onRemoveClicked: (ScheduleOwner) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(owners) {
            ScheduleOwnerItem(
                id = it.networkId,
                name = it.name,
                onEditClicked = {
                    onEditClicked(it)
                },
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
    name: String,
    onEditClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            if (name.isEmpty()) {
                Text(text = id)
            } else {
                Text(text = name)
                Text(
                    text = id,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(onClick = onEditClicked) {
            Icon(Icons.Filled.Edit, "")
        }

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
                    ScheduleOwner("220431", ScheduleOwner.Type.Student, ""),
                    ScheduleOwner("620221",  ScheduleOwner.Type.Student, "Автоматизация+1")
                ),
                onAddClicked = { },
                onEditClicked = { },
                onRemoveClicked = { },
                navBack = { }
            )
        }
    }
}
