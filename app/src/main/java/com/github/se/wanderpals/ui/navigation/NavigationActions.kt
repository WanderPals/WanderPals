package com.github.se.wanderpals.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Suggestion
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

  /**
   * Set the start destination.
   *
   * @param destination The destination to set.
   */
  fun setStartDestination(destination: String) {
    startDestination = destination
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

  var currentRouteTrip = MutableStateFlow(tripNavigation.getStartDestination())

  private var lastlyUsedController = mainNavigation

  /** Update the current route. */
  fun updateCurrentRouteOfTrip(route: String) {
    currentRouteTrip.value = route
  }

  /** Go back in the navigation stack. */
  fun goBack() {
    Log.d("NavigationAction", "goBack before")
    lastlyUsedController.printBackStack()
    lastlyUsedController.goBack()
    Log.d("NavigationAction", "goBack after")
  }

  /**
   * Navigate to a route depending on the route.
   *
   * @param route The route to navigate to.
   */
  fun navigateTo(route: String) {
    if (TRIP_DESTINATIONS.any { it.route == route } && checkIfNavGraphWasSet(tripNavigation)) {
      lastlyUsedController = tripNavigation
      tripNavigation.navigateTo(route)
    } else if (MAIN_ROUTES.any { it == route } && checkIfNavGraphWasSet(mainNavigation)) {
      lastlyUsedController = mainNavigation
      mainNavigation.navigateTo(route)
    }
  }

  /**
   * Check if the nav graph was set.
   *
   * @param nav The navigation state.
   * @return True if the nav graph was set, false otherwise.
   */
  private fun checkIfNavGraphWasSet(nav: MultiNavigationAppState): Boolean {
    return try {
      nav.getNavController.graph
      true
    } catch (e: Exception) {
      Log.d("NavigationAction", "Nav graph was not set")
      false
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
  fun setVariablesSuggestionId(suggestionId: String) {
    variables.suggestionId = suggestionId
  }

  /**
   * Set the variables for the navigation actions.
   *
   * @param suggestion The suggestion.
   */
  fun setVariablesSuggestion(suggestion: Suggestion) {
    variables.suggestionId = suggestion.suggestionId
    variables.currentGeoCords = suggestion.stop.geoCords
    variables.currentSuggestion = suggestion
    variables.currentAddress = suggestion.stop.address
  }

  /**
   * Set the variables for the navigation actions.
   *
   * @param expense The expense.
   */
  fun setVariablesExpense(expense: Expense) {
    variables.expense = expense
  }

  /**
   * Serializes the navigation variables into a string format.
   *
   * @return The serialized string containing navigation variables.
   */
  fun serializeNavigationVariable(): String {
    val navActionsVariablesToString =
        arrayOf(
            "currentTrip: ${variables.currentTrip}",
            "latitude: ${variables.currentGeoCords.latitude}",
            "longitude: ${variables.currentGeoCords.longitude}",
            "currentAddress: ${variables.currentAddress}",
            "suggestionId: ${variables.suggestionId}",
            "expenseId: ${variables.expense.expenseId}")

    return navActionsVariablesToString.joinToString("|")
  }

  /**
   * Deserializes the given string into navigation variables.
   *
   * @param string The serialized string containing navigation variables.
   */
  fun deserializeNavigationVariables(string: String) {
    val variables = NavigationActionsVariables()
    val parts = string.split("|")
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
        "expenseId" -> variables.expense = Expense(expenseId = argVal)
      }
    }
    this.variables = variables
  }
}

/** Variables for the navigation actions. */
class NavigationActionsVariables {
  var currentSuggestion: Suggestion = Suggestion()
  var currentTrip: String = ""
  var currentGeoCords: GeoCords = GeoCords(0.0, 0.0)
  var currentAddress: String = ""
  var suggestionId: String = ""
  var expense = Expense()
}
