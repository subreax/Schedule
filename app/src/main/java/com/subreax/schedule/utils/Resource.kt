package com.subreax.schedule.utils

sealed class Resource<T> {
    class Success<T>(val value: T) : Resource<T>()
    class Failure<T>(val message: UiText, val cachedValue: T? = null) : Resource<T>()

    inline fun <R> ifSuccess(action: (T) -> Resource<R>): Resource<R> {
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

inline fun <C, R> Resource<C>.ifFailure(defaultValue: Resource.Failure<C>.() -> R): R where C : R {
    return when (this) {
        is Resource.Success -> value
        is Resource.Failure -> defaultValue()
    }
}

sealed class LoadResource<T> {
    class Loading<T>(val oldValue: T?) : LoadResource<T>()
    class Success<T>(val value: T) : LoadResource<T>()
    class Failure<T>(val message: UiText, val oldValue: T? = null) : LoadResource<T>()
}