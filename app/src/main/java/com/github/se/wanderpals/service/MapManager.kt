package com.github.se.wanderpals.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.github.se.wanderpals.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.tasks.await

class MapManager(private val context: Context) {

  private val placeFields =
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

  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var placesClient: PlacesClient

  private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>

  private var startingLocation: LatLng = LatLng(46.519653, 6.632273)

  fun initClients() {
    Places.initialize(context, BuildConfig.MAPS_API_KEY)
    placesClient = Places.createClient(context)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
  }

  fun getStartingLocation(): LatLng {
    return startingLocation
  }

  fun changeStartingLocation(newLocation: LatLng) {
    startingLocation = newLocation
  }

  fun setPermissionRequest(request: ActivityResultLauncher<Array<String>>) {
    locationPermissionRequest = request
  }

  fun askLocationPermission() {
    if (::locationPermissionRequest.isInitialized) {
      locationPermissionRequest.launch(
          arrayOf(
              Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
  }

  fun checkLocationPermission(): Boolean {
    return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
  }

  suspend fun getLastLocation(): LatLng? {
    if (!checkLocationPermission()) {
      Log.d("MapActivity", "Location permission not granted")
      return null
    } else {
      val result = fusedLocationClient.lastLocation.await()
      if (result == null) {
        Log.d("MapActivity", "No last known location. Try fetching the current location first")
      } else {
        Log.d(
            "MapActivity",
            "Current location is \n" +
                "lat : ${result.latitude}\n" +
                "long : ${result.longitude}\n" +
                "fetched at ${System.currentTimeMillis()}")
        return LatLng(result.latitude, result.longitude)
      }
    }
    return null
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
        placesClient
            .findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
              onSuccess(response.autocompletePredictions)
            }
            .addOnFailureListener { exception: Exception? -> onFailure(exception) }
      }

  fun fetchPlace(placeId: String): Task<FetchPlaceResponse> {
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)
    return placesClient.fetchPlace(request)
  }
}
