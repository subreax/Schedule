package com.subreax.schedule.ui.home.drawer

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.ScheduleOwner
import com.subreax.schedule.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDrawerContent(
    currentScheduleOwner: ScheduleOwner,
    scheduleOwners: List<ScheduleOwner>,
    onScheduleOwnerClicked: (ScheduleOwner) -> Unit,
    navToScheduleOwnersManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(windowInsets = WindowInsets(0.dp), modifier = modifier) {
        Box(
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Расписание ТулГУ",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Группы",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyMedium
        )

        scheduleOwners.forEach {
            SelectableDrawerItem(
                selected = currentScheduleOwner.networkId == it.networkId,
                onClick = { onScheduleOwnerClicked(it) },
            ) {
                Text(
                    text = it.toPrettyString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        DrawerItem(
            onClick = navToScheduleOwnersManager,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.Tune,
                    contentDescription = "Редактор",
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Открыть редактор".uppercase(),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                )
            }
        }
    }
}

private fun ScheduleOwner.toPrettyString(): String {
    return if (name.isNotEmpty())
        "$name ($networkId)"
    else
        networkId
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeDrawerContentPreview() {
    val scheduleOwners = listOf(
        ScheduleOwner("220431", ScheduleOwner.Type.Student, ""),
        ScheduleOwner("620221", ScheduleOwner.Type.Student, "")
    )

    ScheduleTheme {
        HomeDrawerContent(
            currentScheduleOwner = scheduleOwners.first(),
            scheduleOwners = scheduleOwners,
            onScheduleOwnerClicked = {},
            navToScheduleOwnersManager = {},
            modifier = Modifier.fillMaxHeight(),
        )
    }
}
