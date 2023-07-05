package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightApplication
import com.example.flightsearch.data.FlightRepository
import com.example.flightsearch.data.PreferencesRepository
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.FavoriteItem
import com.example.flightsearch.model.toFavorite
import com.example.flightsearch.model.toFavoriteItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val flightRepository: FlightRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    fun updateQuery(word: String) {
        _searchUiState.update {
            it.copy(
                searchQuery = word
            )
        }
        if (word.isBlank()) setSelectedAirport(null)
    }

    fun saveQuery(query: String) {
        viewModelScope.launch {
            preferencesRepository.saveQueryPreferences(query)
        }
    }

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState

    private suspend fun getAirportByCode(code: String): Airport =
        flightRepository.getAirportByCode(code).first()

    fun setAutoCompleteVisible(visible: Boolean) {
        _searchUiState.update { it.copy(visibleAutoComplete = visible) }
    }

    fun setSelectedAirport(airport: Airport?) {
        _searchUiState.update {
            it.copy(
                searchQuery = airport?.iataCode ?: "",
                selectedAirport = airport,
                visibleAutoComplete = false
            )
        }
        refreshList()
    }

    fun getAirports(query: String): Flow<List<Airport>> = flightRepository.getAirportsByQuery(query)

    private fun getFlights(selectedAirport: Airport) {
        viewModelScope.launch {
            val airports: List<Airport> = flightRepository.getAllAirports().first()
            val flights: MutableList<FavoriteItem> = mutableListOf()
            airports.forEach {
                if (it != selectedAirport) {
                    val favId = selectedAirport.createFavoriteId(it)
                    flights.add(
                        FavoriteItem(
                            favId,
                            selectedAirport.iataCode,
                            selectedAirport.name,
                            it.iataCode,
                            it.name,
                            flightRepository.containsFavorite(favId)
                        )
                    )
                }
            }
            _searchUiState.update {
                it.copy(flightList = flights)
            }
        }
    }


    private fun getFavoriteFlights() {
        viewModelScope.launch {
            _searchUiState.update {
                it.copy(
                    flightList = flightRepository.getAllFavorite().filterNotNull().first()
                        .map { fav ->
                            fav.toFavoriteItem(
                                getAirportByCode(fav.deptCode).name,
                                getAirportByCode(fav.destCode).name
                            )
                        })
            }
        }
    }

    private fun Airport.createFavoriteId(airport: Airport): Int {
        return (this.id.toString() + airport.id.toString()).toInt()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightApplication)
                FlightSearchViewModel(
                    application.appContainer.flightRepository,
                    application.appContainer.preferencesRepository
                )
            }
        }
    }

    init {
        initFlightList()
    }

    private fun initFlightList() {
        viewModelScope.launch {
            val query = preferencesRepository.searchQuery.first()
            updateQuery(query)
            if (query.isBlank()) getFavoriteFlights()
            else {
                val airport = getAirportByCode(query)
                getFlights(airport)
                setSelectedAirport(airport)
            }
        }
    }

    fun toggleFavorite(favoriteItem: FavoriteItem) {
        viewModelScope.launch {
            if (!flightRepository.containsFavorite(favoriteItem.id))
                flightRepository.addFavorite(favoriteItem.toFavorite())
            else
                flightRepository.deleteFavorite(favoriteItem.toFavorite())
            refreshList()
        }
    }

    private fun refreshList() {
        val selectedAirport = searchUiState.value.selectedAirport
        if (selectedAirport == null) getFavoriteFlights()
        else getFlights(selectedAirport)
    }
}

data class SearchUiState(
    val searchQuery: String = "",
    val selectedAirport: Airport? = null,
    val visibleAutoComplete: Boolean = false,
    val flightList: List<FavoriteItem> = listOf()
)