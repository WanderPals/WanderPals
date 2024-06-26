package com.github.se.wanderpals.ui.screens.trip

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
import com.github.se.wanderpals.model.viewmodel.DocumentPSViewModel
import com.github.se.wanderpals.model.viewmodel.FinanceViewModel
import com.github.se.wanderpals.model.viewmodel.MapViewModel
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.model.viewmodel.SessionViewModel
import com.github.se.wanderpals.model.viewmodel.StopsListViewModel
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.service.MapManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.TRIP_BOTTOM_BAR
import com.github.se.wanderpals.ui.screens.docs.DocumentsPS
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionDetail
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionHistoryFeedContent
import com.github.se.wanderpals.ui.screens.trip.agenda.Agenda
import com.github.se.wanderpals.ui.screens.trip.finance.CreateExpense
import com.github.se.wanderpals.ui.screens.trip.finance.ExpenseInfo
import com.github.se.wanderpals.ui.screens.trip.finance.Finance
import com.github.se.wanderpals.ui.screens.trip.map.Map
import com.github.se.wanderpals.ui.screens.trip.notifications.CreateAnnouncement
import com.github.se.wanderpals.ui.screens.trip.notifications.Notification
import com.github.se.wanderpals.ui.screens.trip.stops.StopsList
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

/**
 * Trip screen composable that displays the trip screen with the bottom navigation bar.
 *
 * @param oldNavActions The navigation actions for the previous screen.
 * @param tripId The trip ID.
 * @param tripsRepository The repository for trips data.
 * @param mapManager The map variables.
 */
@Composable
fun Trip(
    oldNavActions: NavigationActions,
    tripId: String,
    tripsRepository: TripsRepository,
    mapManager: MapManager?,
) {

  // update the SessionManagers Users Role, from the User In the Trip Object
  val sessionViewModel: SessionViewModel =
      viewModel(
          factory = SessionViewModel.SessionViewModelFactory(tripsRepository), key = "Session")
  LaunchedEffect(key1 = tripId) {
    sessionViewModel.updateUserForCurrentUser(tripId, oldNavActions)
    sessionViewModel.getTheTokenList(tripId)
  }

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
                      oldNavActions.updateCurrentRouteOfTrip(Route.DASHBOARD)
                      val dashboardViewModel: DashboardViewModel =
                          viewModel(
                              factory =
                                  DashboardViewModel.DashboardViewModelFactory(
                                      tripsRepository, tripId),
                              key = "Dashboard")
                      Dashboard(tripId, dashboardViewModel, oldNavActions)
                    }
                    composable(Route.AGENDA) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.AGENDA)
                      val agendaViewModel: AgendaViewModel =
                          viewModel(
                              factory =
                                  AgendaViewModel.AgendaViewModelFactory(tripId, tripsRepository),
                              key = "Agenda")
                      Agenda(agendaViewModel, tripId, tripsRepository)
                    }
                    composable(Route.SUGGESTION) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.SUGGESTION)
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
                      oldNavActions.updateCurrentRouteOfTrip(Route.MAP)
                      val mapViewModel: MapViewModel =
                          viewModel(
                              factory = MapViewModel.MapViewModelFactory(tripsRepository, tripId))
                      if (mapManager != null) {
                        if (oldNavActions.variables.currentAddress == "") {
                          Log.d("NAVIGATION", "Navigating to map with empty address")
                        } else {
                          Log.d("NAVIGATION", "Navigating to map with address")
                          mapManager.changeStartingLocation(
                              LatLng(
                                  oldNavActions.variables.currentGeoCords.latitude,
                                  oldNavActions.variables.currentGeoCords.longitude))
                        }
                        Map(oldNavActions, mapViewModel, mapManager)
                      }
                    }
                    composable(Route.NOTIFICATION) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.NOTIFICATION)
                      val notificationsViewModel: NotificationsViewModel =
                          viewModel(
                              factory =
                                  NotificationsViewModel.NotificationsViewModelFactory(
                                      tripsRepository, tripId),
                              key = "NotificationsViewModel")
                      Notification(notificationsViewModel, oldNavActions)
                    }
                    composable(Route.CREATE_ANNOUNCEMENT) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.CREATE_ANNOUNCEMENT)
                      val notificationsViewModel: NotificationsViewModel =
                          viewModel(
                              factory =
                                  NotificationsViewModel.NotificationsViewModelFactory(
                                      tripsRepository, tripId),
                              key = "NotificationsViewModel")
                      CreateAnnouncement(
                          notificationsViewModel, onNavigationBack = { oldNavActions.goBack() })
                    }

                    composable(Route.SUGGESTION_DETAIL) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.SUGGESTION_DETAIL)
                      Log.d(
                          "SuggestionDetail",
                          "SuggestionDetail: ${oldNavActions.variables.suggestionId}")

                      val suggestionsViewModel: SuggestionsViewModel =
                          viewModel(
                              factory =
                                  SuggestionsViewModel.SuggestionsViewModelFactory(
                                      tripsRepository, oldNavActions.variables.currentTrip),
                              key = "SuggestionsViewModel")
                      suggestionsViewModel.setSelectedSuggestion(
                          oldNavActions.variables.currentSuggestion)
                      SuggestionDetail(viewModel = suggestionsViewModel, navActions = oldNavActions)
                    }
                    composable(Route.FINANCE) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.FINANCE)
                      val viewModel: FinanceViewModel =
                          viewModel(
                              factory =
                                  FinanceViewModel.FinanceViewModelFactory(
                                      tripsRepository, oldNavActions.variables.currentTrip),
                              key = "FinanceViewModel")
                      Finance(financeViewModel = viewModel, navigationActions = oldNavActions)
                    }
                    composable(Route.CREATE_EXPENSE) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.CREATE_EXPENSE)
                      val viewModel: FinanceViewModel =
                          viewModel(
                              factory =
                                  FinanceViewModel.FinanceViewModelFactory(
                                      tripsRepository, oldNavActions.variables.currentTrip),
                              key = "FinanceViewModel")
                      CreateExpense(tripId, viewModel, oldNavActions) {
                        viewModel.updateStateLists()
                      }
                    }
                    composable(Route.EXPENSE_INFO) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.EXPENSE_INFO)
                      val financeViewModel: FinanceViewModel =
                          viewModel(
                              factory =
                                  FinanceViewModel.FinanceViewModelFactory(
                                      tripsRepository, oldNavActions.variables.currentTrip),
                              key = "FinanceViewModel")

                      financeViewModel.setSelectedExpense(oldNavActions.variables.expense)
                      ExpenseInfo(
                          financeViewModel = financeViewModel,
                      )
                    }
                    composable(Route.STOPS_LIST) {
                      oldNavActions.updateCurrentRouteOfTrip(Route.STOPS_LIST)
                      val viewModel: StopsListViewModel =
                          viewModel(
                              factory =
                                  StopsListViewModel.StopsListViewModelFactory(
                                      tripsRepository, tripId),
                              key = "StopsListViewModel")
                      StopsList(viewModel, tripId, tripsRepository)
                    }
                    composable(Route.DOCUMENT) {
                      // oldNavActions.updateCurrentRouteOfTrip(Route.DOCUMENT)
                      Log.d("DOCUMENTS", "Navigating to Documents")
                      val viewModel: DocumentPSViewModel =
                          viewModel(
                              factory =
                                  DocumentPSViewModel.DocumentPSViewModelFactory(
                                      tripsRepository, tripId),
                              key = "DocumentPSViewModel")
                      DocumentsPS(
                          viewModel = viewModel, storageReference = Firebase.storage.reference)
                    }
                  }
              composable(Route.SUGGESTION_HISTORY) {
                oldNavActions.updateCurrentRouteOfTrip(
                    Route.SUGGESTION_HISTORY) // Update the current route of the trip to
                // SUGGESTION_HISTORY
                val suggestionsViewModel: SuggestionsViewModel =
                    viewModel(
                        factory =
                            SuggestionsViewModel.SuggestionsViewModelFactory(
                                tripsRepository, tripId),
                        key = "SuggestionsHistoryViewModel")
                SuggestionHistoryFeedContent(suggestionsViewModel)
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
  val currentRoute by navActions.currentRouteTrip.collectAsState()

  NavigationBar(
      modifier = Modifier.testTag("bottomNav").height(56.dp),
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSurface,
      tonalElevation = NavigationBarDefaults.Elevation,
      windowInsets = NavigationBarDefaults.windowInsets,
  ) {
    TRIP_BOTTOM_BAR.forEach { destination ->
      val isSelected = currentRoute == destination.route
      NavigationBarItem(
          modifier = Modifier.testTag(destination.text).size(56.dp),
          selected = isSelected,
          onClick = { navActions.navigateTo(destination.route) },
          icon = {
            Image(
                imageVector = if (isSelected) destination.filledIcon else destination.outlinedIcon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface))
          },
          colors =
              NavigationBarItemDefaults.colors(
                  selectedIconColor = MaterialTheme.colorScheme.primary,
                  indicatorColor = MaterialTheme.colorScheme.inversePrimary,
                  unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                  disabledIconColor = MaterialTheme.colorScheme.onSurface,
              ))
    }
  }
}
