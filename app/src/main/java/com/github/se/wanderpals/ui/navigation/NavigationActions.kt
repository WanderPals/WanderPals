package com.github.se.wanderpals.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

class NavigationActions(val navController: NavHostController) {
  // Setup to avoid multiple instances of the same destination, restore previously selected item
  // and pop up the start destination of the graph to avoid a large stack of destinations
  fun navigateTo(route: String) {
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  fun goBack() {
    navController.navigateUp()
  }

  @Composable
  fun getCurrentDestination(): NavDestination? {
      val navBackStackEntry by navController.currentBackStackEntryAsState()
      return navBackStackEntry?.destination
  }
}
