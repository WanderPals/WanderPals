package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.NavigationActions

/**
 * Composable function that represents the content of the overview screen. Displays a list of trips
 * with optional filtering based on search text.
 *
 * @param innerPadding The padding values for the content.
 * @param navigationActions The navigation actions used for navigating to detailed trip view.
 * @param tripsList The list of trips to be displayed.
 * @param searchText The text used for filtering trips by title.
 */
@Composable
fun OverviewContent(
    innerPadding: PaddingValues,
    navigationActions: NavigationActions,
    tripsList: List<Trip>,
    searchText: String,
    overviewViewModel: OverviewViewModel
) {
  // Filter trips by title based on search text
  val filteredTripsByTitle =
      if (searchText.isEmpty()) {
        tripsList
      } else {
        tripsList.filter { trip -> trip.title.lowercase().contains(searchText.lowercase()) }
      }

  // If trips list is empty, display a message
  if (tripsList.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize()) {
      Text(
          modifier =
              Modifier.align(Alignment.Center)
                  .width(260.dp)
                  .height(55.dp)
                  .testTag("noTripForUserText"),
          text =
              when (SessionManager.getIsNetworkAvailable()) {
                true -> "Looks like you have no travel plan yet. "
                false -> "It seems you're not connected to the Internet"
              },
          style =
              TextStyle(
                  fontSize = 18.sp,
                  lineHeight = 20.sp,
                  fontWeight = FontWeight(500),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                  letterSpacing = 0.5.sp,
              ),
      )
      IconButton(
          enabled = SessionManager.getIsNetworkAvailable(),
          onClick = { overviewViewModel.getAllTrips() },
          modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
          content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh trips") })
    }
  } else {
    Column(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
      // If no matching trips found, display a message
      if (filteredTripsByTitle.isEmpty()) {
        Text(
            text = "No trip found.",
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
                    .testTag("noTripFoundOnSearchText"),
            style =
                TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                ))
      } else {
        // Title for the list of trips
        Text(
            text = "My trip projects",
            modifier = Modifier.padding(start = 27.dp, top = 10.dp, bottom = 20.dp),
            style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                ),
            textAlign = TextAlign.Center)

        // LazyColumn to display the list of trips
        val lazyColumn =
            @Composable {
              LazyColumn(
                  Modifier.padding(top = 10.dp, bottom = 20.dp)
                      .fillMaxSize()
                      .testTag("overviewLazyColumn")) {
                    items(filteredTripsByTitle) { trip ->
                      OverviewTrip(
                          trip = trip,
                          navigationActions = navigationActions,
                          overviewViewModel = overviewViewModel)
                    }
                  }
            }
        PullToRefreshLazyColumn(
            inputLazyColumn = lazyColumn, onRefresh = { overviewViewModel.getAllTrips() })
      }
    }
  }
}
