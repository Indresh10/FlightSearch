package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences


class AppDataContainer(private val context: Context, private val dataStore: DataStore<Preferences>) : AppContainer {
    override val flightRepository: FlightRepository by lazy {
        OfflineFlightRepository(FlightDatabase.getInstance(context).dao())
    }
    override val preferencesRepository: PreferencesRepository
        get() = PreferencesRepository(dataStore)
}