package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavAction
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.repository.SuggestionRepository
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import kotlinx.coroutines.Dispatchers

/** The Dashboard screen. */
@Composable
fun Dashboard(tripId: String, suggestionRepository: SuggestionRepository, oldNavAction: NavigationActions) {
  Text(modifier = Modifier.testTag("dashboardScreen"), text = "Dashboard for trip $tripId")
  val navController = rememberNavController()

  // Create a DashboardViewModel and pass the tripId to it
  val dashboardViewModel = DashboardViewModel(suggestionRepository, tripId)


}
