package com.example.flightsearch.data

import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FlightRepository {
    suspend fun addFavorite(favorite: Favorite)

    suspend fun deleteFavorite(favorite: Favorite)

    fun getAllFavorite() : Flow<List<Favorite>>

    suspend fun containsFavorite(id: Int) : Boolean

    fun getAirportsByQuery(query:String): Flow<List<Airport>>

    fun getAirportById(id: Int): Flow<Airport>

    fun getAllAirports():Flow<List<Airport>>

    fun getAirportByCode(code:String):Flow<Airport>
}