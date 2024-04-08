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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers

@Preview(showBackground = true)
@Composable
fun DailyActivitiesPreview() {
  WanderPalsTheme { DailyActivities(AgendaViewModel("", TripsRepository("", Dispatchers.IO))) }
}

/**
 * Composable function that displays the daily activities for a selected date.
 *
 * @param agendaViewModel The view model for managing the agenda of a trip.
 */
@Composable
fun DailyActivities(agendaViewModel: AgendaViewModel) {
  val uiState by agendaViewModel.uiState.collectAsState()
  val selectedDate = uiState.selectedDate

  // Observe the daily activities StateFlow
  val dailyActivities by agendaViewModel.dailyActivities.collectAsState()

  // Trigger data fetch when selectedDate changes
  LaunchedEffect(selectedDate) { selectedDate?.let { agendaViewModel.fetchDailyActivities(it) } }

  DisplayDate(date = selectedDate) // Display the selected date
  // Display daily activities here, using dailyActivities
  // If dailyActivities is empty, display a message
  if (dailyActivities.isEmpty()) {
    Text(
        text = "No activities for this date",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp))
  } else {
    // Display the activities for the selected date
    LazyColumn(content = { items(dailyActivities.sortedBy { it.startTime }) { stop -> ActivityItem(stop) } })
  }
}

/**
 * Composable function that displays an activity item.
 *
 * @param stop The stop to display.
 */
@Composable
fun ActivityItem(stop: Stop) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.secondary)
    Box(
        modifier = Modifier
            .testTag("activityItem")
            .fillMaxWidth()
    ) {
        Button(
            onClick = { /* Handle button click */ },
            shape = RectangleShape,
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Row(modifier = Modifier
                .fillMaxSize()
            ) {
                // Texts Column
                Column(
                    modifier = Modifier
                        .weight(1f) // Takes up all available space, pushing the IconButton to the right
                        .align(Alignment.CenterVertically) // Vertically center the column content
                        .fillMaxSize(), // Fill the available space
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = stop.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        modifier = Modifier
                            .wrapContentWidth(Alignment.Start)
                    )
                    Text(
                        text = "${stop.startTime} - ${stop.startTime.plusMinutes(stop.duration.toLong())}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        modifier = Modifier
                            .wrapContentWidth(Alignment.Start)
                    )
                    Text(
                        text = stop.address,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        modifier = Modifier
                            .wrapContentWidth(Alignment.Start)
                    )
                }

                // Icon Button at the far right, centered vertically
                IconButton(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier
                        .size(24.dp) // Adjust the size of the IconButton as needed
                        .align(Alignment.CenterVertically) // Center the IconButton vertically within the Row
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
}

/**
 * Composable function that displays the selected date.
 *
 * @param date The selected date to display.
 */
@Composable
fun DisplayDate(date: LocalDate?) {

  // Define a formatter
  val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy").withLocale(Locale.getDefault())

  // Format the selected date using the formatter
  val formattedDate = date?.format(formatter) ?: "No date selected"

  // Display the formatted date
  Text(
      text = formattedDate,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.primary,
      textAlign = TextAlign.Center,
      modifier = Modifier
          .padding(16.dp)
          .testTag("displayDateText"))
}
