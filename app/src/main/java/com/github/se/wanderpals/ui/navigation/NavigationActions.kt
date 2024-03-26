package com.github.se.wanderpals.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Class defining the navigation actions in the app.
 *
 * @param navController The navigation controller.
 */
class NavigationActions(val navController: NavHostController) {
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
