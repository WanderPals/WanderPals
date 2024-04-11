package com.github.se.wanderpals.ui.screens.trip

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
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.TRIP_DESTINATIONS
import com.github.se.wanderpals.ui.screens.trip.agenda.Agenda
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
    client: PlacesClient?
) {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)

  Scaffold(
      modifier = Modifier.testTag("tripScreen"),
      topBar = {},
      bottomBar = { BottomBar(navActions) }) { innerPadding ->
        NavHost(navController, startDestination = Route.DASHBOARD, Modifier.padding(innerPadding)) {
          composable(Route.DASHBOARD) { Dashboard(tripId, oldNavActions) }
          composable(Route.AGENDA) { Agenda(AgendaViewModel(tripId)) }
          composable(Route.SUGGESTION) {
            Suggestion(oldNavActions = oldNavActions, tripId, SuggestionsViewModel(tripsRepository, tripId))
          } // todo: might have the param oldNavActions for Suggestion()
          composable(Route.MAP) {
            if (client != null) {
              Map(MapViewModel(tripsRepository, tripId), client)
            }
          }
          composable(Route.NOTIFICATION) { Notification(tripId) }
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
