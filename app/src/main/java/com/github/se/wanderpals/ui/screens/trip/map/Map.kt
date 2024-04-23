package com.github.se.wanderpals.ui.screens.trip.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.github.se.wanderpals.service.MapManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val MAX_PROPOSED_LOCATIONS = 5
const val INIT_PLACE_ID = "ChIJ4zm3ev4wjEcRShTLf2C0UWA"

/**
 * Composable function that represents the Map screen, displaying a map with markers for the stops
 * of a trip and the ability to search for a location.
 *
 * @param oldNavActions The navigation actions for the previous screen.
 * @param mapViewModel The view model containing the data and logic for the map screen.
 * @param mapManager The variables needed for the map screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(
    oldNavActions: NavigationActions,
    mapViewModel: MapViewModel,
    mapManager: MapManager,
) {

  // Google Maps  Variables

  // get the list of stops from the view model
  val stopList by mapViewModel.stops.collectAsState()
  // ui settings for the map
  val uiSettings = remember { MapUiSettings() }
  // see current location of the user on the map
  var seeCurrentLocation by remember { mutableStateOf(false) }
  // current location of the user
  var currentLocation by remember { mutableStateOf(mapManager.getStartingLocation()) }

  // Search Bar Variables

  // variable to extract the search text from the search bar
  var textSearchBar by remember { mutableStateOf("") }
  // active state of the search bar
  var activeSearchBar by remember { mutableStateOf(false) }
  // enable the search bar
  var enabledSearchBar by remember { mutableStateOf(true) }
  // expanded state of the search bar
  var expandedSearchBar by remember { mutableStateOf(false) }
  // proposed location of the searched address List of string of size 5
  var listOfPropositions by remember {
    mutableStateOf(
        List<AutocompletePrediction>(MAX_PROPOSED_LOCATIONS) {
          AutocompletePrediction.builder(INIT_PLACE_ID).build()
        })
  }
  // list of markers on the map from the search bar
  var listOfMarkers by remember { mutableStateOf(listOf<MarkerState>()) }

  // Location Variables

  // location searched by the user
  var searchedLocation by remember { mutableStateOf(mapManager.getStartingLocation()) }
  // final location of the searched location, also the camera position
  var finalLocation by remember { mutableStateOf(mapManager.getStartingLocation()) }
  // name of the searched location
  var finalName by remember { mutableStateOf("") }

  // Bottom Sheet Variables

  // place data of the searched location
  val placeData by remember { mutableStateOf(PlaceData()) }
  // bottom sheet state
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

  // Other

  // coroutine scope to launch coroutines
  val coroutineScope = rememberCoroutineScope()
  // context of the current screen
  val context = LocalContext.current

  // Launch Effects

  LaunchedEffect(Unit) { mapManager.askLocationPermission() }

  LaunchedEffect(key1 = textSearchBar) {
    if (textSearchBar.isBlank()) {
      expandedSearchBar = false
      return@LaunchedEffect
    } else {
      mapManager.getAddressPredictions(
          inputString = textSearchBar,
          location = mapManager.getStartingLocation(),
          onSuccess = { predictions ->
            Log.d("le", "")
            listOfPropositions = predictions
            expandedSearchBar = true
          },
          onFailure = {
            Log.d("Prediction", "Failed")
            expandedSearchBar = false
          })
    }
  }

  // Composable

  Box(Modifier.fillMaxSize().testTag("mapScreen")) {
    // search bar for searching a location on the map and pass it to geocoder
    if (enabledSearchBar) {
      DockedSearchBar(
          // align the search bar to the top left corner with padding
          modifier =
              Modifier.align(Alignment.TopCenter)
                  .padding(top = 8.dp)
                  .shadow(15.dp, shape = RoundedCornerShape(40.dp))
                  .testTag("searchBar"),
          query = textSearchBar,
          onQueryChange = { newText -> textSearchBar = newText },
          enabled = true,
          onSearch = {
            finalLocation = searchedLocation
            finalName = textSearchBar

            listOfMarkers += (MarkerState(position = finalLocation))
            coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
            activeSearchBar = false
          },
          active = activeSearchBar,
          onActiveChange = {
            if (textSearchBar.isEmpty()) {
              expandedSearchBar = false
            }
            activeSearchBar = it
          },
          placeholder = { Text("Search a location") },
          trailingIcon = {
            // if the search text is empty, show the search icon, otherwise show the clear
            // icon
            if (textSearchBar.isEmpty()) {
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = Icons.Default.Search.name,
                  modifier = Modifier.size(24.dp).testTag("searchButtonIcon"))
            } else {
              IconButton(
                  modifier = Modifier.testTag("clearSearchButton"),
                  onClick = { textSearchBar = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = Icons.Default.Clear.name,
                        modifier = Modifier.size(24.dp).testTag("clearSearchButtonIcon"))
                  }
            }
          },
          leadingIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = Icons.Default.Menu.name,
                modifier = Modifier.size(24.dp).testTag("menuButtonIcon"))
          }) {
            // if the search bar is expanded when clicked modify the search text, show the
            // list of suggestions
            if (expandedSearchBar) {
              listOfPropositions.forEach {
                val primaryText = it.getPrimaryText(null).toString()
                val placeId = it.placeId

                Row(modifier = Modifier.padding(8.dp)) {
                  Text(
                      text = primaryText,
                      modifier =
                          Modifier.testTag("listOfPropositions").padding(8.dp).clickable {
                            textSearchBar = primaryText
                            mapManager.fetchPlace(placeId).addOnSuccessListener { response ->
                              val place = response.place
                              placeData.setPlaceData(place)
                              searchedLocation = place.latLng!!
                            }
                          })
                }
              }
            }
          }
    }

    GoogleMap(
        modifier = Modifier.testTag("googleMap").matchParentSize(),
        properties =
            MapProperties(
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)),
        uiSettings = uiSettings.copy(zoomControlsEnabled = false),
        cameraPositionState =
            CameraPositionState(position = CameraPosition(finalLocation, 10f, 0f, 0f))) {

          // display current location on the map
          if (seeCurrentLocation) {
            val resizedIcon =
                Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.resources, R.drawable.current_location),
                    150,
                    150,
                    false)
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Current Location",
                icon = BitmapDescriptorFactory.fromBitmap(resizedIcon))
          }

          // display the marker on the map
          listOfMarkers.forEach { markerState ->
            Marker(
                // Add a marker to the map
                state = markerState,
                title = "Click to Create Suggestions",
                visible = true,
                onInfoWindowClick = {
                  oldNavActions.setVariablesLocation(
                      GeoCords(markerState.position.latitude, markerState.position.longitude),
                      placeData.placeAddress)
                  oldNavActions.navigateTo(Route.CREATE_SUGGESTION)
                })
          }

          // display all the stops on the map
          stopList.forEach {
            Marker( // Add a marker to the map
                state = MarkerState(position = LatLng(it.geoCords.latitude, it.geoCords.longitude)),
                title = it.title,
                snippet = it.description,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
          }
        }
    Switch(
        modifier =
            Modifier.testTag("switchButton")
                .align(AbsoluteAlignment.BottomLeft)
                .padding(horizontal = 16.dp, vertical = 60.dp),
        checked = enabledSearchBar,
        onCheckedChange = { enabledSearchBar = !enabledSearchBar })

    Button(
        onClick = {
          coroutineScope.launch(Dispatchers.IO) {
            mapManager.getLastLocation().let {
              if (it != null) {
                currentLocation = it
                seeCurrentLocation = true
                finalLocation = currentLocation
              }
            }
          }
        },
        modifier =
            Modifier.align(AbsoluteAlignment.BottomRight)
                .padding(horizontal = 16.dp, vertical = 60.dp)) {
          Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Location",
              modifier = Modifier.size(24.dp))
        }

    MapBottomSheet(
        bottomSheetScaffoldState = bottomSheetScaffoldState,
        placeData = placeData,
        uriHandler = LocalUriHandler.current)
  }
}
