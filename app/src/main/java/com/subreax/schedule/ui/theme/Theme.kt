package com.subreax.schedule.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.subreax.schedule.data.model.SubjectType

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

data class ScheduleColors(
    val subjectTeal: Color,
    val subjectGreen: Color,
    val subjectOrange: Color,
    val subjectRed: Color,
    val subjectPink: Color,

    val success: Color,
    val warning: Color,
    val critical: Color
) {
    fun getSubjectColor(type: SubjectType): Color {
        return when (type) {
            SubjectType.Lecture -> subjectTeal

            SubjectType.Practice -> subjectGreen

            SubjectType.Lab -> subjectOrange

            SubjectType.Test,
            SubjectType.DiffTest,
            SubjectType.Exam,
            SubjectType.Consult,
            SubjectType.Coursework -> subjectRed

            else -> subjectPink
        }
    }
}

private val DarkScheduleColors = ScheduleColors(
    subjectTeal = TsuTeal,
    subjectGreen = TsuGreen,
    subjectOrange = TsuOrange,
    subjectRed = TsuRed,
    subjectPink = PinkA200,
    success = SuccessDark,
    warning = WarningDark,
    critical = Red500
)

private val LightScheduleColors = ScheduleColors(
    subjectTeal = Teal400,
    subjectGreen = LightGreen500,
    subjectOrange = Orange500,
    subjectRed = Red500,
    subjectPink = PurpleA100,
    success = SuccessLight,
    warning = WarningLight,
    critical = Red500
)

private val LocalScheduleColors = staticCompositionLocalOf { LightScheduleColors }

@Composable
fun ScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        LaunchedEffect(Unit) {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    val scheduleColors = if (darkTheme) {
        DarkScheduleColors
    } else {
        LightScheduleColors
    }

    CompositionLocalProvider(LocalScheduleColors provides scheduleColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val MaterialTheme.scheduleColors: ScheduleColors
    @Composable
    @ReadOnlyComposable
    get() = LocalScheduleColors.current
