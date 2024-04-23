package com.github.se.wanderpals.ui.map

import android.Manifest
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import com.github.se.wanderpals.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MapVariables(private val context: Context) {
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var placesClient: PlacesClient

  private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>

  private var startingLocation: LatLng = LatLng(46.519653, 6.632273)

  fun initClients() {
    Places.initialize(context, BuildConfig.MAPS_API_KEY)
    placesClient = Places.createClient(context)
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
  }

  fun getFusedLocationClient(): FusedLocationProviderClient {
    return fusedLocationClient
  }

  fun getPlacesClient(): PlacesClient {
    return placesClient
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
    locationPermissionRequest.launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
  }
}
