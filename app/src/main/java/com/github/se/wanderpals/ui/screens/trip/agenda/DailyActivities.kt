package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun DailyActivitiesPreview() {
  WanderPalsTheme { DailyActivities(AgendaViewModel("")) }
}

@Composable
fun DailyActivities(agendaViewModel: AgendaViewModel) {
  val uiState by agendaViewModel.uiState.collectAsState() // Collect the UI state
  val selectedDate = uiState.selectedDate // Get the selected date

  DisplayDate(date = selectedDate) // Display the selected date
}

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
      modifier = Modifier.padding(16.dp))
}
