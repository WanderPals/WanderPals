package com.github.se.wanderpals.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination

class NavigationActions(private val navController: NavController) {
    // Setup to avoid multiple instances of the same destination, restore previously selected item
    // and pop up the start destination of the graph to avoid a large stack of destinations
    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun goBack() {
        navController.navigateUp()
    }

    fun getCurrentDestination(): NavDestination? {
        return navController.currentBackStackEntry?.destination
    }
}