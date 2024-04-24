package com.github.se.wanderpals.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.data.GeoCords

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
    var variables: NavigationActionsVariables = NavigationActionsVariables(),
    var mainNavigation: MultiNavigationAppState = MultiNavigationAppState(),
    var tripNavigation: MultiNavigationAppState = MultiNavigationAppState(),
) {

  private var lastlyUsedController = mainNavigation

  /** Go back in the navigation stack. */
  fun goBack() {
    lastlyUsedController.goBack()
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

  fun serializeNavigationVariable(): String {
    return "currentTrip: ${variables.currentTrip}, " +
        "latitude: ${variables.currentGeoCords.latitude}, " +
        "longitude: ${variables.currentGeoCords.longitude}, " +
        "currentAddress: ${variables.currentAddress}, " +
        "suggestionId: ${variables.suggestionId}"
  }

  fun deserializeNavigationVariables(string: String) {
    val variables = NavigationActionsVariables()
    val parts = string.split(", ")
    for (part in parts) {
      val (argName, argVal) = part.split(": ")
      when (argName) {
        "currentTrip" -> variables.currentTrip = argVal
        "latitude" ->
            variables.currentGeoCords = variables.currentGeoCords.copy(latitude = argVal.toDouble())
        "longitude" ->
            variables.currentGeoCords =
                variables.currentGeoCords.copy(longitude = argVal.toDouble())
        "currentAddress" -> variables.currentAddress = argVal
        "suggestionId" -> variables.suggestionId = argVal
      }
    }
    this.variables = variables
  }
}

/** Variables for the navigation actions. */
class NavigationActionsVariables {
  var currentTrip: String = ""
  var currentGeoCords: GeoCords = GeoCords(0.0, 0.0)
  var currentAddress: String = ""
  var suggestionId: String = ""
}
