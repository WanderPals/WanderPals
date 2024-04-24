package com.github.se.wanderpals.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.data.GeoCords
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * rememberNavController with a start destination for MultiNavigationAppState.
 *
 * @param startDestination The start destination.
 * @param navController The nav controller.
 */
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
    Log.d("NavigationAction", "--------------------")
    navController!!.currentBackStack.value.forEach {
      Log.d("NavigationAction", "screen : ${it.destination.route}")
    }
    Log.d("NavigationAction", "--------------------")
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
    var variables: NavigationActionsVariables = NavigationActionsVariables(),
    var mainNavigation: MultiNavigationAppState = MultiNavigationAppState(),
    var tripNavigation: MultiNavigationAppState = MultiNavigationAppState(),
) {

  var currentRoute = MutableStateFlow(tripNavigation.getStartDestination())

  private var lastlyUsedController = mainNavigation

  /** Update the current route. */
  fun updateCurrentRoute() {
    currentRoute.value =
        lastlyUsedController.getNavController.currentBackStackEntry?.destination?.route.toString()
    if (currentRoute.value == Route.TRIP) {
      currentRoute.value = Route.DASHBOARD
    }
    lastlyUsedController.printBackStack()
  }

  /** Go back in the navigation stack. */
  fun goBack() {
    lastlyUsedController.goBack()
    updateCurrentRoute()
  }

  /**
   * Navigate to a route depending on the route.
   *
   * @param route The route to navigate to.
   */
  fun navigateTo(route: String) {
    if (TRIP_DESTINATIONS.any { it.route == route }) {
      lastlyUsedController = tripNavigation
      tripNavigation.navigateTo(route)
    } else {
      lastlyUsedController = mainNavigation
      mainNavigation.navigateTo(route)
    }
    updateCurrentRoute()
  }

  /**
   * Set the variables for the navigation actions.
   *
   * @param geoCords The geo cords.
   * @param address The address.
   */
  fun setVariablesLocation(geoCords: GeoCords, address: String) {
    variables.currentGeoCords = geoCords
    variables.currentAddress = address
  }

  /**
   * Set the variables for the navigation actions.
   *
   * @param tripId The trip id.
   */
  fun setVariablesTrip(tripId: String) {
    variables.currentTrip = tripId
  }

  /**
   * Set the variables for the navigation actions.
   *
   * @param suggestionId The suggestion id.
   */
  fun setVariablesSuggestion(suggestionId: String) {
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
