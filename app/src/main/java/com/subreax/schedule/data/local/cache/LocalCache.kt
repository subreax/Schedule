package com.subreax.schedule.data.local.cache

interface LocalCache {
    suspend fun get(key: String): String?
    suspend fun set(key: String, value: String)

    suspend fun get(key: String, defaultValue: String): String {
        return get(key) ?: defaultValue
    }
}