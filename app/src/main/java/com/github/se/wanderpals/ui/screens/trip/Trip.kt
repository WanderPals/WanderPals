package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBarDefaults.containerColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.TRIP_DESTINATIONS

@Composable
fun Trip(oldNavActions: NavigationActions) {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)

  Scaffold(topBar = {}, bottomBar = { BottomBar(navActions) }, floatingActionButton = {}) {
      innerPadding ->
    NavHost(navController, startDestination = Route.DASHBOARD, Modifier.padding(innerPadding)) {
      composable(Route.DASHBOARD) { Dashboard() }
      composable(Route.AGENDA) { Agenda() }
      composable(Route.FINANCE) { Finance() }
      composable(Route.MAP) { Map() }
      composable(Route.NOTIFICATION) { Notification() }
    }
  }
}

@Composable
fun BottomBar(navActions: NavigationActions) {
  NavigationBar(
      containerColor = NavigationBarDefaults.containerColor,
      contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor),
      tonalElevation = NavigationBarDefaults.Elevation,
      windowInsets = NavigationBarDefaults.windowInsets,
  ) {

    TRIP_DESTINATIONS.forEach { destination ->
      NavigationBarItem(
          selected = navActions.getCurrentDestination()?.hierarchy?.any { it.route == destination.route } == true,
          onClick = { navActions.navigateTo(destination.route) },
          icon = { Image(imageVector = destination.icon, contentDescription = null) },
          label = { Text(text = destination.route) })
    }
  }
}
