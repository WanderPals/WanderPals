package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    LazyColumn(content = { items(dailyActivities) { stop -> ActivityItem(stop) } })
  }
}

/**
 * Composable function that displays an activity item.
 *
 * @param stop The stop to display.
 */
@Composable
fun ActivityItem(stop: Stop) {
  // Display the stop name
  Text(
      text = stop.title,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(16.dp))

  // Display the stop description
  Text(
      text = stop.description,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.secondary,
      modifier = Modifier.padding(16.dp))
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
      modifier = Modifier.padding(16.dp).testTag("displayDateText"))
}
