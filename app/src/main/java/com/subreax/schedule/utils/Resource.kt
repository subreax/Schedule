package com.subreax.schedule.utils

sealed class Resource<T> {
    class Success<T>(val value: T) : Resource<T>()
    class Failure<T>(val message: UiText, val cachedValue: T? = null): Resource<T>()

    fun <R> mapResult(action: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> {
                Success(action(value))
            }

            is Failure -> {
                Failure(message)
            }
        }
    }
}