package com.github.se.wanderpals.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.data.GeoCords

@Composable
fun rememberMultiNavigationAppState(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) =
    remember(navController, startDestination) {
      MultiNavigationAppState(navController, startDestination)
    }

class MultiNavigationAppState(
    private var navController: NavHostController? = null,
    private var startDestination: String? = null
) {

  /** Navigate back in the navigation stack. */
  fun goBack() {
    navController?.navigateUp()
  }

  /**
   * Get the current destination.
   *
   * @return The current destination.
   */
  @Composable
  fun getCurrentDestination(): NavDestination? {
    return if (navController != null) {
      val navBackStackEntry by navController!!.currentBackStackEntryAsState()
      navBackStackEntry?.destination
    } else {
      null
    }
  }

  /**
   * Set the nav controller.
   *
   * @param inputNavController The nav controller to set.
   */
  fun setNavController(inputNavController: NavHostController) {
    navController = inputNavController
  }

  /**
   * Get the nav controller.
   *
   * @return The nav controller.
   */
  var getNavController: NavHostController = navController!!
    get() {
      return navController!!
    }
    private set

  /**
   * Set the start destination.
   *
   * @param route The route to set as the start destination.
   */
  fun setStartDestination(route: String) {
    startDestination = route
  }

  /**
   * Get the start destination.
   *
   * @return The start destination.
   */
  fun getStartDestination(): String {
    return startDestination!!
  }

  /** Print the back stack. */
  @SuppressLint("RestrictedApi")
  fun printBackStack() {
    println("--------------------")
    navController!!.currentBackStack.value.forEach { println("screen : ${it.destination.route}") }
    println("--------------------")
  }

  /**
   * Navigate to a route.
   *
   * @param route The route to navigate to.
   * @param popUpTo Whether to pop up to the route.
   * @param popUpToInclusive Whether to pop up to the route inclusively.
   */
  fun navigateTo(route: String, popUpTo: Boolean = true, popUpToInclusive: Boolean = false) {
    getNavController.navigate(route) {
      if (popUpTo) {
        popUpTo(route) {
          inclusive = popUpToInclusive
          saveState = false
        }
        launchSingleTop = true
        restoreState = true
      }
    }
  }
}

/** The navigation actions for the app. */
data class NavigationActions(
    var variables: NavigationActionsVariables = globalVariables,
    var mainNavigation: MultiNavigationAppState = MultiNavigationAppState(),
    var tripNavigation: MultiNavigationAppState = MultiNavigationAppState(),
) {

  /**
   * Navigate to a route depending on the route.
   *
   * @param route The route to navigate to.
   */
  fun navigateTo(route: String) {
    if (TRIP_DESTINATIONS.any { it.route == route }) {
      tripNavigation.navigateTo(route)
    } else {
      mainNavigation.navigateTo(route)
    }
  }

  fun navigateToMap(tripId: String, geoCords: GeoCords, address: String) {
    Log.d("NAVIGATION", "Navigating to map")
    mainNavigation.navigateTo(Route.TRIP)
    variables.currentTrip = tripId
    variables.currentGeoCords = geoCords
    variables.currentAddress = address
    variables.suggestionId = ""
  }

  fun navigateToSuggestion(tripId: String, suggestionId: String) {
    Log.d("NAVIGATION", "Navigating to suggestion")
    mainNavigation.navigateTo(Route.TRIP)
    variables.currentTrip = tripId
    variables.currentGeoCords = GeoCords(0.0, 0.0)
    variables.currentAddress = ""
    variables.suggestionId = suggestionId
  }

  fun navigateToCreateSuggestion(tripId: String, geoCords: GeoCords, address: String) {
    Log.d("NAVIGATION", "Navigating to create suggestion")
    mainNavigation.navigateTo(Route.CREATE_SUGGESTION)
    variables.currentTrip = tripId
    variables.currentGeoCords = geoCords
    variables.currentAddress = address
    variables.suggestionId = ""
  }

  fun navigateToSuggestionDetail(suggestionId: String, tripId: String) {
    Log.d("NAVIGATION", "Navigating to suggestion detail")
    tripNavigation.navigateTo(Route.SUGGESTION_DETAIL)
    variables.currentTrip = tripId
    variables.suggestionId = suggestionId
  }
}

/** Variables for the navigation actions. */
class NavigationActionsVariables {
  var currentTrip: String = ""
  var currentGeoCords: GeoCords = GeoCords(0.0, 0.0)
  var currentAddress: String = ""
  var suggestionId: String = ""
}

/** Variables for the navigation actions instance. */
lateinit var globalVariables: NavigationActionsVariables
