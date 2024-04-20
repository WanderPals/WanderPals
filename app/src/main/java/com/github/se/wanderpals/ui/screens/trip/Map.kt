package com.github.se.wanderpals.ui.screens.trip

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
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
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
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
import kotlinx.coroutines.launch

const val MAX_PROPOSED_LOCATIONS = 5
const val INIT_PLACE_ID = "ChIJ4zm3ev4wjEcRShTLf2C0UWA"
val CURRENT_LOCATION = LatLng(46.519653, 6.632273)
/**
 * Composable function that represents the Map screen, displaying a map with markers for the stops
 * of a trip and the ability to search for a location.
 *
 * @param mapViewModel The view model containing the data and logic for the map screen.
 * @param client The PlacesClient for the Google Places API.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(
    oldNavActions: NavigationActions,
    mapViewModel: MapViewModel,
    client: PlacesClient,
    startingLocation: LatLng = CURRENT_LOCATION
) {

  // get the list of stops from the view model
  val stopList by mapViewModel.stops.collectAsState()

  val uiSettings = remember { MapUiSettings() }
  // variable to extract the search text from the search bar
  var searchText by remember { mutableStateOf("") }
  // active state of the search bar
  var active by remember { mutableStateOf(false) }
  // enable the search bar
  var enabled by remember { mutableStateOf(true) }

  // location searched by the user
  var location by remember { mutableStateOf(startingLocation) }

  // expanded state of the search bar
  var expanded by remember { mutableStateOf(false) }
  // proposed location of the searched address List of string of size 5
  var listOfPropositions by remember {
    mutableStateOf(
        List<AutocompletePrediction>(MAX_PROPOSED_LOCATIONS) {
          AutocompletePrediction.builder(INIT_PLACE_ID).build()
        })
  }
  // when the search text is changed, request the location of the address
  var finalLoc by remember { mutableStateOf(startingLocation) }
  // name of the searched location
  var finalName by remember { mutableStateOf("") }

  var visible by remember { mutableStateOf(false) }

  var listOfMarkers by remember { mutableStateOf(listOf<MarkerState>()) }

  // Bottom sheet info
  var placeName by remember { mutableStateOf("") }
  var placeAddress by remember { mutableStateOf("") }
  var placeRating by remember { mutableStateOf("") }
  var placeUserRatingsTotal by remember { mutableStateOf("") }
  var placePhoneNumber by remember { mutableStateOf("") }
  var placeWebsite by remember { mutableStateOf("") }
  var placeOpeningHours by remember { mutableStateOf("") }
  var placeIconUrl by remember { mutableStateOf("") }
  var placeBusinessStatus by remember { mutableStateOf("") }

  // Coroutine scope for the search bar
  val coroutineScope = rememberCoroutineScope()
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
  val uriHandler = LocalUriHandler.current

  val context = LocalContext.current

  // stylish of the map

  LaunchedEffect(key1 = searchText) {
    if (searchText.isBlank()) {
      expanded = false
      return@LaunchedEffect
    } else {
      getAddressPredictions(
          client,
          inputString = searchText,
          location = CURRENT_LOCATION,
          onSuccess = { predictions ->
            Log.d("Prediction", "")
            listOfPropositions = predictions
            expanded = true
          },
          onFailure = {
            Log.d("Prediction", "Failed")
            expanded = false
          })
    }
  }
  Box(Modifier.fillMaxSize().testTag("mapScreen")) {
    // search bar for searching a location on the map and pass it to geocoder
    if (enabled) {
      DockedSearchBar(
          // align the search bar to the top left corner with padding
          modifier =
              Modifier.align(Alignment.TopCenter)
                  .padding(top = 8.dp)
                  .shadow(15.dp, shape = RoundedCornerShape(40.dp))
                  .testTag("searchBar"),
          query = searchText,
          onQueryChange = { newText -> searchText = newText },
          enabled = true,
          onSearch = {
            finalLoc = location
            finalName = searchText

            listOfMarkers += (MarkerState(position = finalLoc))
            coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
            active = false
            visible = true
            // change the camera position to the searched location
          },
          active = active,
          onActiveChange = {
            if (searchText.isEmpty()) {
              expanded = false
            }
            active = it
          },
          placeholder = { Text("Search a location") },
          trailingIcon = {
            // if the search text is empty, show the search icon, otherwise show the clear
            // icon
            if (searchText.isEmpty()) {
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = Icons.Default.Search.name,
                  modifier = Modifier.size(24.dp).testTag("searchButtonIcon"))
            } else {
              IconButton(
                  modifier = Modifier.testTag("clearSearchButton"), onClick = { searchText = "" }) {
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
            if (expanded) {
              listOfPropositions.forEach {
                val primaryText = it.getPrimaryText(null).toString()
                val placeId = it.placeId

                val placeFields =
                    listOf(
                        Place.Field.LAT_LNG,
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.BUSINESS_STATUS,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.RATING,
                        Place.Field.USER_RATINGS_TOTAL,
                        Place.Field.WEBSITE_URI,
                        Place.Field.CURRENT_OPENING_HOURS,
                        Place.Field.ICON_URL)
                Row(modifier = Modifier.padding(8.dp)) {
                  Text(
                      text = primaryText,
                      modifier =
                          Modifier.testTag("listOfPropositions").padding(8.dp).clickable {
                            searchText = primaryText
                            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                            client.fetchPlace(request).addOnSuccessListener { response ->
                              val place = response.place
                              place.name?.let { it1 -> placeName = it1 }
                              place.iconUrl?.let { it1 -> placeIconUrl = it1 }
                              place.businessStatus?.let { it1 ->
                                placeBusinessStatus = it1.toString()
                              }
                              place.phoneNumber?.let { it1 -> placePhoneNumber = it1 }
                              place.address?.let { it1 -> placeAddress = it1 }
                              place.currentOpeningHours?.weekdayText.let { it1 ->
                                placeOpeningHours = it1.toString()
                              }
                              place.rating?.let { it1 -> placeRating = it1.toString() }
                              place.userRatingsTotal?.let { it1 ->
                                placeUserRatingsTotal = it1.toString()
                              }
                              place.websiteUri?.let { it1 -> placeWebsite = it1.toString() }

                              location = place.latLng!!
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
            CameraPositionState(position = CameraPosition(finalLoc, 10f, 0f, 0f))) {
          // display the marker on the map
          listOfMarkers.forEach { markerState ->
            Marker(
                // Add a marker to the map
                state = markerState,
                title = "Click to Create Suggestions",
                visible = visible,
                onInfoWindowClick = {
                  oldNavActions.setVariablesLocation(
                      GeoCords(markerState.position.latitude, markerState.position.longitude),
                      placeAddress)
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
        checked = enabled,
        onCheckedChange = { enabled = !enabled })

    BottomSheetScaffold(
        modifier = Modifier.shadow(15.dp, shape = RoundedCornerShape(40.dp)),
        sheetContent = {
          Column {
            if (placeName.isNotEmpty()) {
              Text(
                  modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp),
                  text = placeName)
            }
            if (placeBusinessStatus.isNotEmpty()) {
              Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Business Status Icon",
                    modifier = Modifier.size(24.dp))
                Text(text = placeBusinessStatus)
              }
            }
            if (placeAddress.isNotEmpty()) {
              Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Place Icon",
                    modifier = Modifier.size(24.dp))
                Text(text = placeAddress)
              }
            }
            if (placeRating.isNotEmpty()) {

              Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating Icon",
                    modifier = Modifier.size(24.dp))
                Text(text = "$placeRating/5.0")
              }
            }
            if (placeUserRatingsTotal.isNotEmpty()) {
              Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Total Icon",
                    modifier = Modifier.size(24.dp))
                Text(text = placeUserRatingsTotal)
              }
            }
            if (placePhoneNumber.isNotEmpty()) {
              Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone Icon",
                    modifier = Modifier.size(24.dp))
                Text(text = placePhoneNumber)
              }
            }
            if (placeWebsite.isNotEmpty()) {
              Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "Visit Website: ")
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Phone Icon",
                    modifier = Modifier.size(24.dp).clickable { uriHandler.openUri(placeWebsite) })
              }
            }

            if (placeOpeningHours.isNotEmpty()) {
              Spacer(modifier = Modifier.height(16.dp))
              val listOfDays = placeOpeningHours.removePrefix("[").removeSuffix("]").split(", ")
              listOfDays.forEach {
                if (it != "null") {
                  Text(
                      modifier =
                          Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                      text = it)
                }
              }
            }
          }
        },
        scaffoldState = bottomSheetScaffoldState) {}
  }
}
/**
 * Function to get the address predictions from the Places API.
 *
 * @param client The PlacesClient used to search for locations.
 * @param sessionToken The session token used for the autocomplete request.
 * @param inputString The input string to search for.
 * @param location The location to bias the search results.
 * @param onSuccess The function to call when the predictions are successfully retrieved.
 * @param onFailure The function to call when the predictions retrieval fails.
 */
suspend fun getAddressPredictions(
    client: PlacesClient,
    sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance(),
    inputString: String,
    location: LatLng,
    onSuccess: (List<AutocompletePrediction>) -> Unit = {},
    onFailure: (Exception?) -> Unit = { throw Exception("Place not found") }
) =
    suspendCoroutine<Unit> {
      val request =
          FindAutocompletePredictionsRequest.builder()
              .setLocationBias(
                  location.let { locationBias ->
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
