package com.example.flightsearch.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.FavoriteItem

@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory)
) {
    val uiState: SearchUiState by viewModel.searchUiState.collectAsState()
    val searchAirports =
        viewModel.getAirports(uiState.searchQuery).collectAsState(initial = emptyList())
    Column(modifier = Modifier.fillMaxWidth()) {
        SearchInput(
            query = uiState.searchQuery,
            onTextChange = {
                viewModel.updateQuery(it)
                viewModel.setAutoCompleteVisible(it.isNotBlank())
                if (it.isBlank()) viewModel.saveQuery(it)
            }, modifier = Modifier.fillMaxWidth()
        )
        Box {
            AirportList(
                list = searchAirports.value,
                onClick = {
                    viewModel.setSelectedAirport(airport = it)
                    viewModel.saveQuery(query = it.iataCode)
                },
                modifier = Modifier.fillMaxSize(),
                visible = uiState.visibleAutoComplete,
                onBack = {
                    viewModel.setAutoCompleteVisible(false)
                }
            )
            val flightList = if (uiState.visibleAutoComplete) emptyList() else uiState.flightList
            FlightList(
                flightList = flightList,
                selectedAirport = uiState.selectedAirport?.iataCode,
                visible = !uiState.visibleAutoComplete,
                onIconClick = {
                    viewModel.toggleFavorite(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInput(query: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onTextChange,
        placeholder = {
            Text(text = stringResource(id = R.string.search))
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        trailingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier.padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBoxPreview() {
    SearchInput(query = "", onTextChange = {})
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AirportList(
    visible: Boolean,
    list: List<Airport>,
    onClick: (Airport) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible)
        BackHandler {
            onBack()
        }
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        LazyColumn(modifier = modifier) {
            if(list.isNotEmpty())
                itemsIndexed(list) { index, airport ->
                    SearchItem(
                        airport = airport,
                        onClick = { onClick(airport) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateEnterExit(
                                enter = slideInVertically(animationSpec = spring(
                                    stiffness = Spring.StiffnessVeryLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                ), initialOffsetY = { it * index + 1 })
                            )
                    )
                }
            else
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()) {
                        Text(text = stringResource(id = R.string.sorry), style = MaterialTheme.typography.displaySmall)
                        Text(text = stringResource(id = R.string.no_result), style = MaterialTheme.typography.titleSmall)
                    }
                }
                
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchListPreview() {
    val mockList = List(8) { Airport(it, "$it", "Airport$it", 100) }
    AirportList(
        true,
        list = mockList,
        onClick = {},
        onBack = {},
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SearchItem(airport: Airport, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.airport_search, airport.iataCode, airport.name),
        modifier = modifier
            .clickable {
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun FlightItem(
    flightItem: FavoriteItem,
    onIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            FlightData(
                flightItem.deptName,
                flightItem.deptCode,
                flightItem.destName,
                flightItem.destCode,
                modifier = Modifier.weight(1f)
            )
            FavoriteButton(isFavorite = flightItem.isFavorite, onIconClick = onIconClick)
        }
    }
}

@Composable
fun FlightData(
    fromAirport: String,
    fromAirportCode: String,
    toAirport: String,
    toAirportCode: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.depart).uppercase(),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(
                id = R.string.airport_search,
                fromAirportCode,
                fromAirport
            )
        )
        Text(
            text = stringResource(id = R.string.arrive).uppercase(),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(
                id = R.string.airport_search,
                toAirportCode,
                toAirport
            )
        )
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onIconClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onIconClick, modifier = modifier) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Star else Icons.TwoTone.Star,
            contentDescription = if (isFavorite) "Favorites" else "Not Favorites",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FlightList(
    flightList: List<FavoriteItem>,
    selectedAirport: String?,
    visible: Boolean,
    onIconClick: (FavoriteItem) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                val title: String = if (selectedAirport.isNullOrEmpty())
                    stringResource(id = R.string.favorite_flights)
                else
                    stringResource(id = R.string.flight_from, selectedAirport)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
            itemsIndexed(flightList) { index, data ->
                FlightItem(
                    flightItem = data, modifier = Modifier
                        .padding(start = 16.dp)
                        .animateEnterExit(
                            enter = slideInHorizontally(animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                dampingRatio = Spring.DampingRatioNoBouncy
                            ), initialOffsetX = { it * index + 1 })
                        ), onIconClick = { onIconClick(data) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlightListPreview() {
    val mockList =
        List(8) { FavoriteItem(it, "ABC", "ABC Airport", "XY${it}", "XY${it} Airport", false) }
    FlightList(flightList = mockList, selectedAirport = "ABC", true, {})
}