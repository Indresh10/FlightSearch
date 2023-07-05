package com.example.flightsearch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val searchQueryKey = stringPreferencesKey("query")
        const val TAG = "PreferencesRepository"
    }

    suspend fun saveQueryPreferences(query: String) {
        dataStore.edit {
            it[searchQueryKey] = query
        }
    }

    val searchQuery: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error loading preferences", it)
                emit(emptyPreferences())
            }
        }
        .map {
            it[searchQueryKey] ?: ""
        }
}