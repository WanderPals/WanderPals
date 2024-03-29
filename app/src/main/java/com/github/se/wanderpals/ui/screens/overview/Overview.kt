package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.format.DateTimeFormatter


/**
 * Composable function that represents the Overview screen, displaying the list of trips of a user.
 * Provides functionalities such as searching trips by their title, creating a new trip,
 * and joining a trip.
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
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    } else {
        // Display scaffold with top bar, bottom bar, and content when data is loaded
        Scaffold(
            topBar = {
                // Top bar with search functionality based on the title of the trips
                OverviewTopBar(
                    searchText = searchText,
                    onSearchTextChanged = { newSearchText -> searchText = newSearchText }
                )
            },
            bottomBar = {
                // Bottom bar containing buttons to create a new trip and join a trip
                OverviewBottomBar(
                    onCreateTripClick = { navigationActions.navigateTo(Route.CREATE_TRIP) },
                    onLinkClick = {/*TODO do this when starting the link invitation*/ })
            }
        ) {
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
