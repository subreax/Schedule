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

        val maxWidthPx = constraints.maxWidth
        val gapPx = gap.roundToPx()

        val maxTitleWidthPx = measurables.first().maxIntrinsicWidth(constraints.maxHeight)
        val maxNoteWidthPx = measurables.getOrNull(1)?.maxIntrinsicWidth(constraints.maxHeight) ?: 0
        val defaultMinNoteWidthPx = (maxWidthPx * MaxNoteWidthFraction).roundToInt()
        val minNoteWidthPx = minOf(defaultMinNoteWidthPx, maxNoteWidthPx)


        val titleWidthPx = if (maxNoteWidthPx == 0) {
            maxTitleWidthPx.coerceAtMost(maxWidthPx)
        } else {
            maxTitleWidthPx.coerceAtMost(maxWidthPx - gapPx - minNoteWidthPx)
        }

        val noteWidthPx = (maxWidthPx - titleWidthPx - gapPx).coerceAtLeast(0)

        val titlePlaceable = measurables.first().measure(
            constraints.copy(
                minWidth = titleWidthPx,
                maxWidth = titleWidthPx,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )

        val notePlaceable = measurables.getOrNull(1)?.measure(
            constraints.copy(
                minWidth = noteWidthPx,
                maxWidth = noteWidthPx,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )

        val width = constraints.constrainWidth(titleWidthPx + noteWidthPx)
        val height =
            constraints.constrainHeight(maxOf(titlePlaceable.height, notePlaceable?.height ?: 0))
        layout(width, height) {
            var x = 0f
            titlePlaceable.place(0, 0)
            x += titlePlaceable.width

            if (notePlaceable != null && notePlaceable.width > 0) {
                x += gap.toPx()
                val y = (height - notePlaceable.height) / 2f
                notePlaceable.place(x.roundToInt(), y.roundToInt())
            }
        }
    }
}


@Composable
private fun PreviewText(text: String) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TitleLayoutPreview() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    PreviewText("Заголовок")
                },
                note = {
                    PreviewText("(Примечание)")
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
                    PreviewText("Длинный заголовок")
                },
                note = {
                    PreviewText("(Примечание)")
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
                    PreviewText(text = "Длинный заголовок заголовок")
                },
                note = {
                    PreviewText(text = "(Очень длинный адрес)")
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
private fun ExtraLongNoteTitleLayoutPreview2() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    PreviewText(text = "Заголовок")
                },
                note = {
                    PreviewText(text = "(Очень длинный адрес)")
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
private fun TitleLayoutPreview3() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    PreviewText(text = "Длинный заголовок фффф")
                },
                note = {
                    PreviewText(text = "(1п/г)")
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
private fun TitleLayoutPreview4() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    PreviewText(text = "Длинный заголовок очень длинный")
                },
                note = {
                    PreviewText(text = "")
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
private fun TitleLayoutPreview5() {
    ScheduleTheme {
        Surface {
            TitleLayout(
                title = {
                    PreviewText(text = "")
                },
                note = {
                    PreviewText(text = "Длинное примечание примечание выфафыввы")
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(240.dp)
            )
        }
    }
}