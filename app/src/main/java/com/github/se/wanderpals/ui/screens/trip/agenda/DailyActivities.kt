package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.screens.trip.stops.StopItem


/**
 * Composable function that displays the daily activities for a selected date.
 *
 * @param agendaViewModel The view model for managing the agenda of a trip.
 * @param onActivityItemClick Callback function triggered when an activity item is clicked,
 */
@Composable
fun DailyActivities(agendaViewModel: AgendaViewModel, onActivityItemClick: (String) -> Unit, tripId: String, tripsRepository: TripsRepository) {
  val uiState by agendaViewModel.uiState.collectAsState()
  val selectedDate = uiState.selectedDate

  // Observe the daily activities StateFlow
  val dailyActivities by agendaViewModel.dailyActivities.collectAsState()

  val refreshFunction = { selectedDate?.let { agendaViewModel.fetchDailyActivities(it) } }

  // Trigger data fetch when selectedDate changes
  LaunchedEffect(selectedDate) { refreshFunction() }

  // Display daily activities here, using dailyActivities
  // If dailyActivities is empty, display a message
  if (dailyActivities.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize()) {
      Text(
          text = "No activities for this date",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.padding(16.dp).testTag("NoActivitiesMessage").align(Alignment.Center))
      IconButton(
          onClick = { refreshFunction() },
          modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
          content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh trips") })
    }
  } else {
    // Display the activities for the selected date
    val lazyColumn =
        @Composable {
          LazyColumn(
              content = {
                items(dailyActivities.sortedBy { it.startTime }) { stop ->
                  StopItem(stop, onActivityItemClick, tripId, tripsRepository,{refreshFunction()})
                }
              })
        }
    PullToRefreshLazyColumn(lazyColumn, { refreshFunction() })
  }
}
