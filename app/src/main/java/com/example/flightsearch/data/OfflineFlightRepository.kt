package com.example.flightsearch.data

import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.Favorite
import kotlinx.coroutines.flow.Flow

class OfflineFlightRepository(private val dao: FlightDao) : FlightRepository {
    override suspend fun addFavorite(favorite: Favorite) = dao.addFavorite(favorite)

    override suspend fun deleteFavorite(favorite: Favorite) = dao.deleteFavorite(favorite)

    override fun getAllFavorite(): Flow<List<Favorite>>  = dao.getAllFavorite()

    override suspend fun containsFavorite(id: Int): Boolean = dao.getFavoriteCount(id) == 1

    override fun getAirportsByQuery(query: String): Flow<List<Airport>> = dao.getAirportsByQuery(query)

    override fun getAirportById(id: Int): Flow<Airport> = dao.getAirportById(id)

    override fun getAllAirports(): Flow<List<Airport>>  = dao.getAllAirports()

    override fun getAirportByCode(code: String): Flow<Airport> = dao.getAirportByCode(code)

}