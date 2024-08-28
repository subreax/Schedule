package com.subreax.schedule.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.subreax.schedule.R
import com.subreax.schedule.data.model.SubjectType

private val SubjectTypesToRes = listOf(
    R.string.lecture,
    R.string.practice,
    R.string.lab,
    R.string.test,
    R.string.diff_test,
    R.string.exam,
    R.string.consult
)

@Composable
fun SubjectType.toLocalizedString(): String = toLocalizedString(LocalContext.current)

fun SubjectType.toLocalizedString(context: Context): String {
    return if (ordinal < SubjectTypesToRes.size) {
        context.getString(SubjectTypesToRes[ordinal])
    } else {
        id
    }
}
