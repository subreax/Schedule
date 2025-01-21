package com.subreax.schedule.data.local.cache.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.subreax.schedule.data.local.cache.LocalCache
import kotlinx.coroutines.flow.first

class DataStoreLocalCache(private val dataStore: DataStore<Preferences>) : LocalCache {
    override suspend fun get(key: String): String? {
        val prefs = dataStore.data.first()
        val strKey = stringPreferencesKey(key)
        return prefs[strKey]
    }

    override suspend fun set(key: String, value: String) {
        val strKey = stringPreferencesKey(key)
        dataStore.edit { prefs ->
            prefs[strKey] = value
        }
    }

    companion object {
        const val FILE_NAME = "schedule.local"
    }
}