package com.github.se.wanderpals.ui.screens.trip

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBarDefaults.containerColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AdminViewModel
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.TRIP_DESTINATIONS
import com.github.se.wanderpals.ui.screens.Admin
import com.github.se.wanderpals.ui.screens.trip.agenda.Agenda
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient

/**
 * Trip screen composable that displays the trip screen with the bottom navigation bar.
 *
 * @param oldNavActions The navigation actions for the previous screen.
 * @param tripId The trip ID.
 * @param tripsRepository The repository for trips data.
 * @param client The PlacesClient for the Google Places API.
 */
@Composable
fun Trip(
    oldNavActions: NavigationActions,
    tripId: String,
    tripsRepository: TripsRepository,
    client: PlacesClient?,
    startingRoute: String = Route.DASHBOARD
) {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)

  Scaffold(
      modifier = Modifier.testTag("tripScreen"),
      topBar = {},
      bottomBar = { BottomBar(navActions) }) { innerPadding ->
        NavHost(navController, startDestination = startingRoute, Modifier.padding(innerPadding)) {
          composable(Route.DASHBOARD) {
            BackHandler(true) {}
            Dashboard(
                tripId, DashboardViewModel(tripsRepository, tripId), oldNavActions, navActions)
          }
          composable(Route.AGENDA) {
            BackHandler(true) {}
            Agenda(AgendaViewModel(tripId, tripsRepository))
          }
          composable(Route.SUGGESTION) {
            BackHandler(true) {}
            Suggestion(
                oldNavActions,
                tripId,
                SuggestionsViewModel(tripsRepository, tripId),
                onSuggestionClick = {
                  oldNavActions.navigateToCreateSuggestion(tripId, GeoCords(0.0, 0.0), "")
                })
          }
          composable(Route.MAP) {
            BackHandler(true) {}
            if (client != null) {
              if (navActions.variables.currentAddress == "") {
                Log.d("NAVIGATION", "Navigating to map with empty address")
                Map(oldNavActions, MapViewModel(tripsRepository, tripId), client)
              } else {
                Log.d("NAVIGATION", "Navigating to map with address")
                val latLong =
                    LatLng(
                        navActions.variables.currentGeoCords.latitude,
                        navActions.variables.currentGeoCords.longitude)
                Map(oldNavActions, MapViewModel(tripsRepository, tripId), client, latLong)
              }
            }
          }
          composable(Route.NOTIFICATION) {
            BackHandler(true) {}
            Notification(tripId)
          }
        }
      }
}

/**
 * Bottom navigation bar composable that displays the bottom navigation bar.
 *
 * @param navActions The navigation actions for the screen.
 */
@Composable
fun BottomBar(navActions: NavigationActions) {
  NavigationBar(
      modifier = Modifier.testTag("bottomNav").height(56.dp),
      containerColor = NavigationBarDefaults.containerColor,
      contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor),
      tonalElevation = NavigationBarDefaults.Elevation,
      windowInsets = NavigationBarDefaults.windowInsets,
  ) {
    TRIP_DESTINATIONS.forEach { destination ->
      NavigationBarItem(
          modifier = Modifier.testTag(destination.text).size(56.dp),
          selected =
              navActions.getCurrentDestination()?.hierarchy?.any {
                it.route == destination.route
              } == true,
          onClick = { navActions.navigateTo(destination.route) },
          icon = { Image(imageVector = destination.icon, contentDescription = null) },
      )
    }
  }
}
