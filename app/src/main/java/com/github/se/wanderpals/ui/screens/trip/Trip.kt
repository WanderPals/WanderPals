package com.github.se.wanderpals.ui.screens.trip

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.github.se.wanderpals.model.viewmodel.MembersViewModel
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.model.viewmodel.SessionViewModel
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.TRIP_BOTTOM_BAR
import com.github.se.wanderpals.ui.screens.dashboard.DashboardMemberList
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionDetail
import com.github.se.wanderpals.ui.screens.trip.agenda.Agenda
import com.github.se.wanderpals.ui.screens.trip.notifications.Notification
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
    client: PlacesClient?
) {

  // update the SessionManagers Users Role, from the User In the Trip Object
  val sessionViewModel: SessionViewModel =
      viewModel(
          factory = SessionViewModel.SessionViewModelFactory(tripsRepository), key = "Session")
  LaunchedEffect(key1 = tripId) { sessionViewModel.updateRoleForCurrentUser(tripId) }

  Scaffold(
      modifier = Modifier.testTag("tripScreen"),
      topBar = {},
      bottomBar = { BottomBar(oldNavActions) }) { innerPadding ->
        oldNavActions.tripNavigation.setNavController(rememberNavController())
        NavHost(
            oldNavActions.tripNavigation.getNavController,
            startDestination = Route.TRIP,
            route = Route.ROOT_ROUTE,
            modifier = Modifier.padding(innerPadding)) {
              navigation(
                  startDestination = oldNavActions.tripNavigation.getStartDestination(),
                  route = Route.TRIP) {
                    composable(Route.DASHBOARD) {
                      val dashboardViewModel: DashboardViewModel =
                          viewModel(
                              factory =
                                  DashboardViewModel.DashboardViewModelFactory(
                                      tripsRepository, tripId),
                              key = "Dashboard")
                      Dashboard(tripId, dashboardViewModel, oldNavActions)
                    }
                    composable(Route.AGENDA) {
                      val agendaViewModel: AgendaViewModel =
                          viewModel(
                              factory =
                                  AgendaViewModel.AgendaViewModelFactory(tripId, tripsRepository),
                              key = "Agenda")
                      Agenda(agendaViewModel)
                    }
                    composable(Route.SUGGESTION) {
                      val suggestionsViewModel: SuggestionsViewModel =
                          viewModel(
                              factory =
                                  SuggestionsViewModel.SuggestionsViewModelFactory(
                                      tripsRepository, tripId),
                              key = "SuggestionsViewModel")
                      Suggestion(
                          oldNavActions,
                          tripId,
                          suggestionsViewModel,
                          onSuggestionClick = {
                            oldNavActions.setVariablesSuggestion(
                                com.github.se.wanderpals.model.data.Suggestion())
                            oldNavActions.navigateTo(Route.CREATE_SUGGESTION)
                          })
                    }
                    composable(Route.MAP) {
                      val mapViewModel: MapViewModel =
                          viewModel(
                              factory = MapViewModel.MapViewModelFactory(tripsRepository, tripId))
                      if (client != null) {
                        if (oldNavActions.variables.currentAddress == "") {
                          Log.d("NAVIGATION", "Navigating to map with empty address")

                          Map(oldNavActions, mapViewModel, client)
                        } else {
                          Log.d("NAVIGATION", "Navigating to map with address")
                          val latLong =
                              LatLng(
                                  oldNavActions.variables.currentGeoCords.latitude,
                                  oldNavActions.variables.currentGeoCords.longitude)
                          Map(oldNavActions, mapViewModel, client, latLong)
                        }
                      }
                    }
                    composable(Route.NOTIFICATION) {
                      Notification(NotificationsViewModel(tripsRepository))
                    }

                    composable(Route.MEMBERS) {
                      DashboardMemberList(
                          MembersViewModel(tripsRepository, oldNavActions.variables.currentTrip),
                          oldNavActions)
                    }

                    composable(Route.SUGGESTION_DETAIL) {
                      Log.d(
                          "SuggestionDetail",
                          "SuggestionDetail: ${oldNavActions.variables.suggestionId}")

                      val suggestionsViewModel: SuggestionsViewModel =
                          viewModel(
                              factory =
                                  SuggestionsViewModel.SuggestionsViewModelFactory(
                                      tripsRepository, oldNavActions.variables.currentTrip),
                              key = "SuggestionsViewModel")
                      SuggestionDetail(
                          suggestionId = oldNavActions.variables.suggestionId,
                          viewModel = suggestionsViewModel,
                          navActions = oldNavActions)
                    }
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
    var currentRoute by remember { mutableStateOf(Route.DASHBOARD) }

    TRIP_BOTTOM_BAR.forEach { destination ->
      NavigationBarItem(
          modifier = Modifier.testTag(destination.text).size(56.dp),
          selected = currentRoute == destination.route,
          onClick = {
            currentRoute = destination.route
            navActions.navigateTo(destination.route)
          },
          icon = { Image(imageVector = destination.icon, contentDescription = null) },
      )
    }
  }
}
