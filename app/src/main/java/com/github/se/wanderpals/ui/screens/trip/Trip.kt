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
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.TRIP_DESTINATIONS
import com.github.se.wanderpals.ui.screens.trip.agenda.Agenda

/** The Trip screen. */
@Composable
fun Trip(oldNavActions: NavigationActions, tripId: String, tripsRepository: TripsRepository) {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)

  Scaffold(
      modifier = Modifier.testTag("tripScreen"),
      topBar = {},
      bottomBar = { BottomBar(navActions) }) { innerPadding ->
        NavHost(navController, startDestination = Route.DASHBOARD, Modifier.padding(innerPadding)) {
          composable(Route.DASHBOARD) { Dashboard(tripId) }
          composable(Route.AGENDA) { Agenda(AgendaViewModel(tripId)) }
          composable(Route.SUGGESTION) {
            Suggestion(tripId, SuggestionsViewModel(tripsRepository, tripId))
          } // todo: might have the param oldNavActions for Suggestion()
          composable(Route.MAP) { Map(tripId) }
          composable(Route.NOTIFICATION) { Notification(tripId) }
        }
      }
}

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
