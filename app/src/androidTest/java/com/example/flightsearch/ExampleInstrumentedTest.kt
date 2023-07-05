package com.example.flightsearch

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.flightsearch.data.FlightDatabase
import com.example.flightsearch.data.FlightRepository
import com.example.flightsearch.data.OfflineFlightRepository
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.Favorite
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var flightRepository: FlightRepository
    private lateinit var db:FlightDatabase
    private val items:Array<Airport> = arrayOf(
        Airport(1,"ABC","ABC Airport",1000),
        Airport(2,"DEF","DEF Airport",1000),
        Airport(3,"XYZ","XYZ Airport",1000)
    )
    @Before
    fun createRepository() = runBlocking {
        val context : Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context,FlightDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db.dao().insertAirport(*items)
        flightRepository = OfflineFlightRepository(db.dao())
    }

    @Test
    @Throws(Exception::class)
    fun getAirport(): Unit = runBlocking {
        val list = flightRepository.getAllAirports().first()
        assertTrue(list.isNotEmpty())
        var airport = flightRepository.getAirportById(1).first()
        assertEquals(airport.id,1)
        airport = flightRepository.getAirportByCode("ABC").first()
        assertEquals(airport.iataCode,"ABC")
        airport = flightRepository.getAirportsByQuery("ABC").first().first()
        assertEquals(airport.id,1)
    }

    @Test
    @Throws(Exception::class)
    fun checkFavorites()= runBlocking {
        flightRepository.addFavorite(Favorite(1,items[0].iataCode,items[1].iataCode))
        flightRepository.addFavorite(Favorite(2,items[0].iataCode,items[2].iataCode))
        var fav = flightRepository.getAllFavorite().first()
        assertEquals(fav.size,2)
        flightRepository.deleteFavorite(Favorite(1,items[0].iataCode,items[1].iataCode))
        fav = flightRepository.getAllFavorite().first()
        assertEquals(fav.size,1)
        assertNotNull(flightRepository.containsFavorite(2))
    }

    @After
    @Throws(IOException::class)
    fun closeDB(){
        db.close()
    }
}