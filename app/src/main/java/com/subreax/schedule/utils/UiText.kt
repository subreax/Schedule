package com.subreax.schedule.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed class UiText {
    abstract override fun toString(): String
    abstract fun toString(context: Context): String

    companion object {
        fun hardcoded(str: String): UiText {
            return Hardcoded(str)
        }

        fun res(@StringRes stringRes: Int, vararg args: Any): UiText {
            return Res(stringRes, args)
        }
    }

    private class Hardcoded(private val str: String) : UiText() {
        override fun toString(): String = str
        override fun toString(context: Context): String = str
    }

    private class Res(
        @StringRes private val stringRes: Int,
        private val args: Array<out Any> = arrayOf()
    ) : UiText() {
        override fun toString(): String {
            return "res#$stringRes"
        }

        override fun toString(context: Context): String {
            return context.getString(stringRes, *args)
        }
    }
}

@Composable
fun UiText.toLocalizedString(): String {
    return toString(LocalContext.current)
}