package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Insert
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("Select * from favorite")
    fun getAllFavorite() : Flow<List<Favorite>>

    @Query("select count(*) from favorite where id = :id")
    suspend fun getFavoriteCount(id: Int) : Int

    @Query("select * from airport where iata_code like '%'||:query||'%' or name like '%'||:query||'%'")
    fun getAirportsByQuery(query:String): Flow<List<Airport>>

    @Query("select * from airport where id = :id limit 1")
    fun getAirportById(id: Int): Flow<Airport>

    @Query("select * from airport order by passengers desc")
    fun getAllAirports():Flow<List<Airport>>

    @Query("select * from  airport where iata_code = :code limit 1")
    fun getAirportByCode(code:String):Flow<Airport>

    // for testing only
    @Insert
    suspend fun insertAirport(vararg airport: Airport)
}