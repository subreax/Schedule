package com.subreax.schedule.utils

sealed class Resource<T> {
    class Success<T>(val value: T) : Resource<T>()
    class Failure<T>(val message: UiText, val cachedValue: T? = null) : Resource<T>()

    suspend fun <R> ifSuccess(action: suspend (T) -> Resource<R>): Resource<R> {
        return when (this) {
            is Success -> {
                action(value)
            }

            is Failure -> {
                Failure(message)
            }
        }
    }

    fun requireValue(): T {
        return (this as Success).value
    }
}

sealed class LoadResource<T> {
    class Loading<T>(val oldValue: T?) : LoadResource<T>()
    class Success<T>(val value: T) : LoadResource<T>()
    class Failure<T>(val message: UiText, val oldValue: T? = null) : LoadResource<T>()
}