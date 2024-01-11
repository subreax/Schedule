package com.subreax.schedule.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.ui.component.TypeIndicator
import com.subreax.schedule.ui.theme.ScheduleTheme
import com.subreax.schedule.utils.join
import kotlin.math.roundToInt

private val subjectHeight = 44.dp

private val indexModifier = Modifier
    .width(30.dp)
    .padding(end = 8.dp)

private val typeIndicatorModifier = Modifier
    .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
    .width(4.dp)
    .fillMaxHeight()

@Composable
fun SubjectItem(
    index: String,
    name: String,
    infoItem1: String,
    infoItem2: String,
    type: SubjectType,
    note: String?,
    onSubjectClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infoText = remember {
        val typeStr = if (type.ordinal > SubjectType.Lab.ordinal) type.name else ""
        " • ".join(infoItem1, typeStr, infoItem2)
    }

    SubjectItem(
        index = index,
        title = name,
        subtitle = infoText,
        type = type,
        note = note,
        onSubjectClicked = onSubjectClicked,
        modifier = modifier
    )
}

@Composable
private fun SubjectItem(
    index: String,
    title: String,
    subtitle: String,
    type: SubjectType,
    note: String?,
    onSubjectClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onSubjectClicked)
            .then(modifier)
            .height(subjectHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (index.length < 2) {
            Index(value = index, modifier = indexModifier)
        } else {
            TimeIndex(value = index, modifier = indexModifier)
        }

        TypeIndicator(
            type = type,
            modifier = typeIndicatorModifier
        )

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            TitleRow(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                note = {
                    if (note != null) {
                        Text(
                            text = "($note)",
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Index(value: String, modifier: Modifier = Modifier) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun TimeIndex(value: String, modifier: Modifier = Modifier) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        lineHeight = 16.sp
    )
}


@Composable
private fun TitleRow(
    modifier: Modifier = Modifier,
    gap: Dp = 4.dp,
    title: @Composable () -> Unit,
    note: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = { title(); note(); }
    ) { measurables, constraints ->
        if (measurables.isEmpty()) {
            error("Place at least one component in TitleRow")
        }
        if (measurables.size > 2) {
            error("Do not use this layout with 3 or more children")
        }

        val constraints0 = constraints.copy(minWidth = 0, minHeight = 0)
        val notePlaceable = measurables.getOrNull(1)?.measure(constraints0)

        val noteWidth = if (notePlaceable != null) {
            notePlaceable.width + gap.roundToPx()
        } else 0

        val noteHeight = notePlaceable?.height ?: 0

        val constraints1 = constraints0.copy(maxWidth = constraints.maxWidth - noteWidth)
        val titlePlaceable = measurables.first().measure(constraints1)

        val width = constraints.constrainWidth(titlePlaceable.width + noteWidth)
        val height = constraints.constrainHeight(maxOf(titlePlaceable.height, noteHeight))
        layout(width, height) {
            var x = 0f
            titlePlaceable.place(0, 0)
            x += titlePlaceable.width

            if (notePlaceable != null) {
                x += gap.toPx()
                val y = (height - notePlaceable.height) / 2f
                notePlaceable.place(x.roundToInt(), y.roundToInt())
            }
        }
    }
}

@Preview(widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SubjectPreview() {
    ScheduleTheme {
        Surface {
            SubjectItem(
                index = "1",
                name = "Математический анал",
                infoItem1 = "Гл.-431",
                infoItem2 = "Кузнецова В. А.",
                type = SubjectType.Lecture,
                note = "прим",
                onSubjectClicked = {},
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}