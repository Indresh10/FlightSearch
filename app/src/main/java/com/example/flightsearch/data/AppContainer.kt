package com.example.flightsearch.data

interface AppContainer {
    val flightRepository: FlightRepository
    val preferencesRepository : PreferencesRepository
}