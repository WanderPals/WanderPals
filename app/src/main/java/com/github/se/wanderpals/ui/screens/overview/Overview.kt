package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route

/**
 * Composable function that represents the Overview screen, displaying the list of trips of a user.
 * Provides functionalities such as searching trips by their title, creating a new trip, and joining
 * a trip.
 *
 * @param overviewViewModel The view model containing the data and logic for the overview screen.
 * @param navigationActions The navigation actions used for navigating to different screens.
 */
@Composable
fun Overview(overviewViewModel: OverviewViewModel, navigationActions: NavigationActions) {

  // Collecting trips list and loading state from view model
  val tripsList by overviewViewModel.state.collectAsState()
  val isLoading by overviewViewModel.isLoading.collectAsState()

  // State for managing search text
  var searchText by remember { mutableStateOf("") }

  // Display loading indicator waiting for database to fetch the trips of the user
  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier.size(50.dp).align(Alignment.Center))
    }
  } else {
    // Display scaffold with top bar, bottom bar, and content when data is loaded
    Scaffold(
        modifier = Modifier.testTag("overviewScreen"),
        topBar = {
          // Top bar with search functionality based on the title of the trips
          OverviewTopBar(
              searchText = searchText,
              onSearchTextChanged = { newSearchText -> searchText = newSearchText })
        },
        bottomBar = {
          // Bottom bar containing buttons to create a new trip and join a trip
          OverviewBottomBar(
              onCreateTripClick = { navigationActions.navigateTo(Route.CREATE_TRIP) },
              onLinkClick = { /*TODO do this when starting the link invitation*/})
        }) {
            // Content of the overview screen
            innerPadding ->
          OverviewContent(
              innerPadding = innerPadding,
              navigationActions = navigationActions,
              tripsList = tripsList,
              searchText = searchText)
        }
  }
}
