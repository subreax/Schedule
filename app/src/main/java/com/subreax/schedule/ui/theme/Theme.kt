package com.subreax.schedule.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ColorScheme.subjectColorFrom(type: SubjectType): Color {
    return if (isSystemInDarkTheme()) {
        when (type) {
            SubjectType.Lecture -> TsuTeal

            SubjectType.Practice -> TsuGreen

            SubjectType.Lab -> TsuOrange

            SubjectType.Test,
            SubjectType.DiffTest,
            SubjectType.Exam,
            SubjectType.Consult,
            SubjectType.Coursework -> TsuRed

            else -> PinkA200
        }
    } else {
        when (type) {
            SubjectType.Lecture -> Teal400

            SubjectType.Practice -> LightGreen500

            SubjectType.Lab -> Orange500

            SubjectType.Test,
            SubjectType.DiffTest,
            SubjectType.Exam,
            SubjectType.Consult,
            SubjectType.Coursework -> Red500

            else -> PurpleA100
        }
    }
}

val ColorScheme.success: Color
    @Composable get() = if (isSystemInDarkTheme()) {
        SuccessDark
    } else {
        SuccessLight
    }

val ColorScheme.warning: Color
    @Composable get() = if (isSystemInDarkTheme()) {
        WarningDark
    } else {
        WarningLight
    }