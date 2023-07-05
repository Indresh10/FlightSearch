package com.example.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.AppContainer
import com.example.flightsearch.data.AppDataContainer
const val PREFERENCES_NAME = "query_preferences"
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(PREFERENCES_NAME)
class FlightApplication : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(applicationContext,dataStore)
    }
}