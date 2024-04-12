package com.github.se.wanderpals.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.se.wanderpals.model.data.GeoCords

/**
 * Class defining the navigation actions in the app.
 *
 * @param navController The navigation controller.
 */
class NavigationActions(private val navController: NavHostController) {

  var variables = NavigationActionsVariables()

  /**
   * Navigate to a specific route.
   *
   * Setup to avoid multiple instances of the same destination, restore previously selected item and
   * pop up the start destination of the graph to avoid a large stack of destinations
   *
   * @param route The route to navigate to.
   */
  fun navigateTo(route: String) {
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  fun navigateToTrip(tripId: String) {
    navController.navigate(Route.TRIP) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
      variables.currentTrip = tripId
      variables.currentGeoCords = GeoCords(0.0, 0.0)
      variables.currentAddress = ""
    }
  }

  fun navigateToMap(tripId: String, geoCords: GeoCords, address: String) {
    Log.d("NAVIGATION", "Navigating to map")
    navController.navigate(Route.TRIP) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
      variables.currentTrip = tripId
      variables.currentGeoCords = geoCords
      variables.currentAddress = address
      variables.suggestionId = ""
    }
  }

  fun navigateToSuggestion(tripId: String, suggestionId: String) {
    Log.d("NAVIGATION", "Navigating to suggestion")
    navController.navigate(Route.TRIP) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
      variables.currentTrip = tripId
      variables.currentGeoCords = GeoCords(0.0, 0.0)
      variables.currentAddress = ""
      variables.suggestionId = suggestionId
    }
  }

  fun navigateToCreateSuggestion(tripId: String, geoCords: GeoCords, address: String) {
    Log.d("NAVIGATION", "Navigating to create suggestion")
    navController.navigate(Route.CREATE_SUGGESTION) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
      variables.currentTrip = tripId
      variables.currentGeoCords = geoCords
      variables.currentAddress = address
      variables.suggestionId = ""
    }
  }

  /** Navigate back in the navigation stack. */
  fun goBack() {
    navController.navigateUp()
  }

  /**
   * Get the current destination.
   *
   * @return The current destination.
   */
  @Composable
  fun getCurrentDestination(): NavDestination? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination
  }
}

class NavigationActionsVariables {
  var currentTrip: String = ""
  var currentGeoCords: GeoCords = GeoCords(0.0, 0.0)
  var currentAddress: String = ""
  var suggestionId: String = ""
}
