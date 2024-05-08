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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import com.github.se.wanderpals.ui.theme.outlineVariantLight
import com.github.se.wanderpals.ui.theme.secondaryLight
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
fun DailyActivities(agendaViewModel: AgendaViewModel, onActivityItemClick: (String) -> Unit) {
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
                  ActivityItem(stop, onActivityItemClick)
                }
              })
        }
    PullToRefreshLazyColumn(lazyColumn, { refreshFunction() })
  }
}

/**
 * Composable function that displays an activity item.
 *
 * @param stop The stop to display.
 * @param onActivityClick Callback function triggered when the activity item is clicked,
 */
@Composable
fun ActivityItem(stop: Stop, onActivityClick: (String) -> Unit) {
  val stopHasLocation = stop.geoCords.latitude != 0.0 || stop.geoCords.longitude != 0.0
  Box(modifier = Modifier.testTag(stop.stopId).fillMaxWidth()) {
    Button(
        onClick = { onActivityClick(stop.stopId) },
        shape = RectangleShape,
        modifier =
            Modifier.height(100.dp).fillMaxWidth().testTag("activityItemButton" + stop.stopId),
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
                      style =
                          TextStyle(
                              fontSize = 16.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              letterSpacing = 0.16.sp),
                      color = MaterialTheme.colorScheme.primary,
                      modifier =
                          Modifier.wrapContentWidth(Alignment.Start)
                              .testTag("ActivityTitle" + stop.stopId))
                  Text(
                      text =
                          "${stop.startTime} - ${stop.startTime.plusMinutes(stop.duration.toLong())}",
                      style =
                          TextStyle(
                              fontSize = 16.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              letterSpacing = 0.16.sp,
                          ),
                      color = MaterialTheme.colorScheme.secondary,
                      modifier =
                          Modifier.wrapContentWidth(Alignment.Start)
                              .testTag("ActivityTime" + stop.stopId))
                  if (stopHasLocation) {
                    Text(
                        text = stop.address,
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(500),
                                letterSpacing = 0.16.sp),
                        color = secondaryLight,
                        modifier =
                            Modifier.wrapContentWidth(Alignment.Start)
                                .testTag("ActivityAddress" + stop.stopId))
                  }
                }

            // Icon Button at the far right, centered vertically
            IconButton(
                onClick = {
                  if (stopHasLocation) {
                    navigationActions.setVariablesLocation(stop.geoCords, stop.address)
                    navigationActions.navigateTo(Route.MAP)
                  }
                },
                modifier =
                    Modifier.size(24.dp) // Adjust the size of the IconButton as needed
                        .align(Alignment.CenterVertically)
                        .testTag(
                            "navigationToMapButton" +
                                stop.stopId), // Center the IconButton vertically within the
                enabled = stopHasLocation
                // Row
                ) {
                  Icon(
                      imageVector = Icons.Default.LocationOn,
                      tint = if (stopHasLocation) secondaryLight else Color.LightGray,
                      contentDescription = null // Provide an appropriate content description
                      )
                }
          }
        }
  }

  // the horizontal divider
  Box(
      modifier =
          Modifier.fillMaxWidth(), // This ensures the box takes the full width of its container
      contentAlignment = Alignment.Center // This will center the content inside the box
      ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(), // Customize this width as needed
            thickness = 1.dp,
            color = outlineVariantLight)
      }
}
