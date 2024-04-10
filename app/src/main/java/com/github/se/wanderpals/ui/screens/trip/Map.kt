package com.github.se.wanderpals.ui.screens.trip

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlin.coroutines.suspendCoroutine

/**
 * Composable function that represents the Map screen, displaying a map with markers for the stops
 * of a trip and the ability to search for a location.
 *
 * @param tripId The ID of the trip to display the stops for.
 * @param mapViewModel The view model containing the data and logic for the map screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(tripId: String, mapViewModel: MapViewModel, client: PlacesClient) {
  var uiSettings by remember { mutableStateOf(MapUiSettings()) }
  // variable to extract the search text from the search bar
  var searchText by remember { mutableStateOf("") }
  // active state of the search bar
  var active by remember { mutableStateOf(false) }
  // enable the search bar
  var enabled by remember { mutableStateOf(true) }

  // location searched by the user
  var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }

  // expanded state of the search bar
  var expanded by remember { mutableStateOf(false) }
  // proposed location of the searched address List of string of size 5
  var listOfPropositions by remember {
    mutableStateOf(
        List<AutocompletePrediction>(5) {
          AutocompletePrediction.builder(" ChIJ4zm3ev4wjEcRShTLf2C0UWA").build()
        })
  }
  // when the search text is changed, request the location of the address
  var finalLoc by remember { mutableStateOf(LatLng(0.0, 0.0)) }
  // name of the searched location
  var finalName by remember { mutableStateOf("") }

  var visible by remember { mutableStateOf(false) }

  var listOfMarkers by remember { mutableStateOf(listOf<MarkerState>()) }

  LaunchedEffect(key1 = searchText) {
    if (searchText.isBlank()) {
      expanded = false
      return@LaunchedEffect
    }
    getAddressPredictions(
        client,
        inputString = searchText,
        location = LatLng(46.519653, 6.632273),
        onSuccess = { predictions ->
          Log.d("Prediction", "")
          for (prediction in predictions) {
            Log.d("Prediction", prediction.getFullText(null).toString())
          }
          listOfPropositions = predictions
          expanded = true
        },
        onFailure = {
          Log.d("Prediction", "Failed")
          expanded = false
        })
  }

  // Get the location of the searched address
  Box(Modifier.fillMaxSize().testTag("mapScreen")) {
    // search bar for searching a location on the map and pass it to geocoder
    if (enabled) {
      DockedSearchBar(
          // align the search bar to the top left corner with padding
          modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
          query = searchText,
          onQueryChange = { newText -> searchText = newText },
          enabled = true,
          onSearch = {
            active = false
            visible = true
            // change the camera position to the searched location
            finalLoc = location
            finalName = searchText
          },
          active = active,
          onActiveChange = { active = it },
          placeholder = { Text("Search a location") },
          trailingIcon = {
            // if the search text is empty, show the search icon, otherwise show the clear icon
            if (searchText.isEmpty()) {
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = Icons.Default.Search.name,
                  modifier = Modifier.size(24.dp))
            } else {
              IconButton(
                  modifier = Modifier.testTag("clearSearchButton"), onClick = { searchText = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = Icons.Default.Clear.name,
                        modifier = Modifier.size(24.dp))
                  }
            }
          },
          leadingIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = Icons.Default.Menu.name,
                modifier = Modifier.size(24.dp))
          }) {
            // if the search bar is expanded when clicked modify the search text, show the list of
            // suggestions
            if (expanded) {
              listOfPropositions.forEach {
                val primaryText = it.getPrimaryText(null).toString()
                val placeId = it.placeId
                val placeFields = listOf(Place.Field.LAT_LNG)
                Column {
                  Button(
                      onClick = {
                        searchText = primaryText
                        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                        client.fetchPlace(request).addOnSuccessListener { response ->
                          val place = response.place
                          location = place.latLng!!
                        }
                      }) {
                        Text(text = primaryText)
                      }
                }
              }
            }
          }
    }
    GoogleMap(
        modifier = Modifier.matchParentSize(),
        properties = MapProperties(mapType = MapType.NORMAL),
        uiSettings = uiSettings,
        cameraPositionState =
            CameraPositionState(position = CameraPosition(finalLoc, 10f, 0f, 0f))) {
          // add the marker to the list of markers
          listOfMarkers += (MarkerState(position = finalLoc))
          // display the marker on the map
          listOfMarkers.forEach {
            Marker(
                // Add a marker to the map
                state = it,
                title = String.format("%S", finalName),
                snippet = "Population: 883,305",
                visible = visible,
            )
          }

          // display all the stops on the map
          mapViewModel.getAllStops(tripId).forEach {
            Marker( // Add a marker to the map
                state = MarkerState(position = LatLng(it.geoCords.latitude, it.geoCords.longitude)),
                title = it.title,
                snippet = it.description)
          }
        }
    Switch(
        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
        checked = uiSettings.zoomControlsEnabled,
        onCheckedChange = {
          uiSettings = uiSettings.copy(zoomControlsEnabled = it)
          enabled = !enabled
        })
  }
}

suspend fun getAddressPredictions(
    client: PlacesClient,
    sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance(),
    inputString: String,
    location: LatLng? = null,
    onSuccess: (List<AutocompletePrediction>) -> Unit = {},
    onFailure: (Exception?) -> Unit = { throw Exception("Place not found") }
) =
    suspendCoroutine<Unit> {
      val request =
          FindAutocompletePredictionsRequest.builder()
              .setLocationBias(
                  location?.let { locationBias ->
                    RectangularBounds.newInstance(
                        LatLng(locationBias.latitude - 0.1, locationBias.longitude - 0.1),
                        LatLng(locationBias.latitude + 0.1, locationBias.longitude + 0.1))
                  })
              .setOrigin(location)
              .setCountries("CH")
              .setSessionToken(sessionToken)
              .setQuery(inputString)
              .build()
      client
          .findAutocompletePredictions(request)
          .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
            onSuccess(response.autocompletePredictions)
          }
          .addOnFailureListener { exception: Exception? -> onFailure(exception) }
    }
