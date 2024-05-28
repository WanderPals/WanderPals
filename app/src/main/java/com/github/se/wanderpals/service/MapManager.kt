package com.github.se.wanderpals.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.github.se.wanderpals.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Class to manage the map and location services.
 *
 * @param context The context of the activity.
 */
class MapManager(private val context: Context) {

  private val _position = MutableStateFlow(LatLng(0.0, 0.0))
  val position: Flow<LatLng> = _position.asStateFlow()

  private val _isTracking = MutableStateFlow(false)
  val isTracking: Flow<Boolean> = _isTracking.asStateFlow()

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

  private lateinit var locationIntentStart: () -> Unit
  private lateinit var locationIntentStop: () -> Unit

  /** Function to initialize the Places API and the FusedLocationProviderClient. */
  fun initClients() {
    if (!Places.isInitialized()) {
      Places.initialize(context, BuildConfig.MAPS_API_KEY)
    }
    if (!::placesClient.isInitialized) {
      placesClient = Places.createClient(context)
    }
    if (!::fusedLocationClient.isInitialized) {
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }
  }

  /**
   * Function to get the starting location of the map.
   *
   * @return The starting location of the map.
   */
  fun getStartingLocation(): LatLng {
    return startingLocation
  }

  /** Update the position of the user. */
  fun updatePosition(latLng: LatLng) {
    _position.value = latLng
  }

  /**
   * Function to set the starting location intent.
   *
   * @param intent The location intent to set.
   */
  fun setLocationIntentStart(intent: () -> Unit) {
    locationIntentStart = intent
  }

  /**
   * Function to set the stopping location intent.
   *
   * @param intent The location intent to set.
   */
  fun setLocationIntentStop(intent: () -> Unit) {
    locationIntentStop = intent
  }

  /** Function to execute the location intent. */
  fun executeLocationIntent() {
    _isTracking.value = true
    locationIntentStart().run {}
  }

  /** Function to execute the location intent. */
  fun executeLocationIntentStop() {
    _isTracking.value = false
    locationIntentStop().run {}
  }

  /**
   * Function to get the location updates.
   *
   * @param interval The interval between location updates.
   * @return The flow of location updates.
   */
  fun getLocationUpdates(interval: Long): Flow<Location> {
    return callbackFlow {
      if (!checkLocationPermission()) {
        throw Exception("Location permission is required")
      }

      val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
      val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
      val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
      if (!isGpsEnabled && !isNetworkEnabled) {
        throw Exception("Location services are disabled")
      }

      val request =
          LocationRequest.create()
              .setInterval(interval)
              .setFastestInterval(interval)
              .setPriority(100)

      val locationCallback =
          object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
              super.onLocationResult(result)
              result.locations.lastOrNull()?.let { location -> launch { send(location) } }
            }
          }

      if (checkLocationPermission()) {
        fusedLocationClient.requestLocationUpdates(
            request, locationCallback, Looper.getMainLooper())
      }

      awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }
  }

  /**
   * Function to change the starting location of the map.
   *
   * @param newLocation The new location to set as the starting location.
   */
  fun changeStartingLocation(newLocation: LatLng) {
    startingLocation = newLocation
  }

  /**
   * Function to set the permission request for location.
   *
   * @param request The permission request for location.
   */
  fun setPermissionRequest(request: ActivityResultLauncher<Array<String>>) {
    locationPermissionRequest = request
  }

  /** Function to ask for location permission. */
  fun askLocationPermission() {
    if (::locationPermissionRequest.isInitialized) {
      if (checkLocationPermission()) {
        Log.d("MapActivity", "Location permission already granted")
      } else {
        Log.d("MapActivity", "Requesting location permission")
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
      }
    }
  }

  /**
   * Function to check if the location permission is granted.
   *
   * @return True if the location permission is granted, false otherwise.
   */
  private fun checkLocationPermission(): Boolean {
    return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
  }

  /**
   * Function to get the last known location of the device.
   *
   * @return The last known location of the device.
   */
  suspend fun getLastLocation(): LatLng? {
    if (!checkLocationPermission()) {
      Log.d("MapActivity", "Location permission not granted")
      return null
    } else {
      val result =
          fusedLocationClient
              .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
              .await()
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

  /**
   * Function to fetch a place from the Places API.
   *
   * @param placeId The ID of the place to fetch.
   * @return The response of the fetch place request.
   */
  fun fetchPlace(placeId: String): Task<FetchPlaceResponse> {
    Log.d("MapActivity", "Fetching place with ID: $placeId")
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)
    return placesClient.fetchPlace(request)
  }
}
