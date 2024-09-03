package com.subreax.schedule.ui.component.schedule.item.subject

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.subreax.schedule.ui.theme.ScheduleTheme
import kotlin.math.roundToInt

private const val MaxNoteWidthFraction = 1f / 3f

@Composable
fun TitleLayout(
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

        val maxNoteWidth = (constraints.maxWidth * MaxNoteWidthFraction).roundToInt()
        val notePlaceable = measurables.getOrNull(1)?.measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = maxNoteWidth
            )
        )
        val noteWidth = notePlaceable?.width?.plus(gap.roundToPx()) ?: 0
        val noteHeight = notePlaceable?.height ?: 0

        val titlePlaceable = measurables.first().measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = constraints.maxWidth - noteWidth
            )
        )
        val titleWidth = titlePlaceable.width
        val titleHeight = titlePlaceable.height

        val width = constraints.constrainWidth(titleWidth + noteWidth)
        val height = constraints.constrainHeight(maxOf(titleHeight, noteHeight))
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleLayoutPreview() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    Text(text = "Заголовок")
                },
                note = {
                    Text(text = "Примечание")
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(240.dp)
            )
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LongTitleLayoutPreview() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    Text(
                        text = "Длинный заголовок",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                note = {
                    Text(text = "Примечание")
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(240.dp)
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExtraLongNoteTitleLayoutPreview() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    Text(
                        text = "Длинный заголовок",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                note = {
                    Text(text = "ТулГУ: А давай тут адрес хренакнем, чтобы приложуха крашилась")
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(240.dp)
            )
        }
    }
}
