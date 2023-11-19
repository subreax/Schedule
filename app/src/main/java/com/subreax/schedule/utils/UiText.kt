package com.subreax.schedule.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    abstract override fun toString(): String
    abstract fun toString(context: Context): String

    companion object {
        fun hardcoded(str: String): UiText {
            return Hardcoded(str)
        }

        fun res(@StringRes stringRes: Int, args: Array<Any> = emptyArray()): UiText {
            return Res(stringRes, args)
        }
    }

    private class Hardcoded(private val str: String) : UiText() {
        override fun toString(): String = str
        override fun toString(context: Context): String = str
    }

    private class Res(
        @StringRes private val stringRes: Int,
        private val args: Array<Any> = arrayOf()
    ) : UiText() {
        override fun toString(): String {
            return "res#$stringRes"
        }

        override fun toString(context: Context): String {
            return context.getString(stringRes, *args)
        }
    }
}