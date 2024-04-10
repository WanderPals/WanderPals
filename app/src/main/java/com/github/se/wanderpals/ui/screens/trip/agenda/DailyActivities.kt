package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import kotlinx.coroutines.Dispatchers

@Preview(showBackground = true)
@Composable
fun DailyActivitiesPreview() {
  WanderPalsTheme { DailyActivities(AgendaViewModel("", TripsRepository("", Dispatchers.IO))) {} }
}

/**
 * Composable function that displays the daily activities for a selected date.
 *
 * @param agendaViewModel The view model for managing the agenda of a trip.
 * @param onActivityItemClick Callback function triggered when an activity item is clicked,
 */
@Composable
fun DailyActivities(agendaViewModel: AgendaViewModel,onActivityItemClick: (String) -> Unit) {
  val uiState by agendaViewModel.uiState.collectAsState()
  val selectedDate = uiState.selectedDate

  // Observe the daily activities StateFlow
  val dailyActivities by agendaViewModel.dailyActivities.collectAsState()

  // Trigger data fetch when selectedDate changes
  LaunchedEffect(selectedDate) { selectedDate?.let { agendaViewModel.fetchDailyActivities(it) } }

  // Display daily activities here, using dailyActivities
  // If dailyActivities is empty, display a message
  if (dailyActivities.isEmpty()) {
    Text(
        text = "No activities for this date",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp).testTag("NoActivitiesMessage"))
  } else {
    // Display the activities for the selected date
    LazyColumn(
        content = {
          items(dailyActivities.sortedBy { it.startTime }) { stop -> ActivityItem(stop,onActivityItemClick) }
        })
  }
}

/**
 * Composable function that displays an activity item.
 *
 * @param stop The stop to display.
 * @param onActivityClick Callback function triggered when the activity item is clicked,
 */
@Composable
fun ActivityItem(stop: Stop,onActivityClick: (String) -> Unit) {
  Box(modifier = Modifier.testTag(stop.stopId).fillMaxWidth()) {
    Button(
        onClick = { onActivityClick(stop.stopId) },
        shape = RectangleShape,
        modifier = Modifier.height(100.dp).fillMaxWidth().testTag("activityItemButton"+stop.stopId),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
          Row(modifier = Modifier.fillMaxSize()) {
            // Texts Column
            Column(
                modifier =
                    Modifier.weight(
                            1f) // Takes up all available space, pushing the IconButton to the right
                        .align(Alignment.CenterVertically) // Vertically center the column content
                        .fillMaxSize(), // Fill the available space
                verticalArrangement = Arrangement.SpaceEvenly) {
                  Text(
                      text = stop.title,
                      style = MaterialTheme.typography.bodyLarge,
                      color = Color.Black,
                      modifier = Modifier
                          .wrapContentWidth(Alignment.Start)
                          .testTag("ActivityTitle" + stop.stopId))
                  Text(
                      text =
                          "${stop.startTime} - ${stop.startTime.plusMinutes(stop.duration.toLong())}",
                      style = MaterialTheme.typography.bodyLarge,
                      color = Color.Black,
                      modifier = Modifier
                          .wrapContentWidth(Alignment.Start)
                          .testTag("ActivityTime" + stop.stopId))
                  Text(
                      text = stop.address,
                      style = MaterialTheme.typography.bodyLarge,
                      color = Color.Black,
                      modifier = Modifier
                          .wrapContentWidth(Alignment.Start)
                          .testTag("ActivityAddress" + stop.stopId))
                }

            // Icon Button at the far right, centered vertically
            IconButton(
                onClick = { /* Handle button click */},
                modifier =
                    Modifier.size(24.dp) // Adjust the size of the IconButton as needed
                        .align(
                            Alignment
                                .CenterVertically) // Center the IconButton vertically within the
                                                   // Row
                ) {
                  Icon(
                      imageVector = Icons.Default.LocationOn,
                      tint = MaterialTheme.colorScheme.primary,
                      contentDescription = null // Provide an appropriate content description
                      )
                }
          }
        }
  }
  HorizontalDivider(
      modifier = Modifier.fillMaxWidth(),
      thickness = 1.dp,
      color = MaterialTheme.colorScheme.secondary)
}
