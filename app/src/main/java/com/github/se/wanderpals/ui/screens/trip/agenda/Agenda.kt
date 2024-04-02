package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import java.time.YearMonth

private const val DAYS_IN_A_WEEK = 7
private const val MAX_ROWS_CALENDAR = 6

@Preview(showSystemUi = true)
@Composable
fun AgendaPreview() {
  WanderPalsTheme { Agenda(AgendaViewModel("")) }
}

/**
 * The main entry point Composable for the Agenda screen. It displays a calendar view that allows
 * users to navigate through months and select dates. This screen is part of the WanderPals
 * application, focusing on trip planning and scheduling.
 *
 * @param agendaViewModel The ViewModel that holds and manages UI-related data for the Agenda
 *   screen. It defaults to a ViewModel instance provided by the `viewModel()` function.
 */
@Composable
fun Agenda(agendaViewModel: AgendaViewModel) {
  val uiState by agendaViewModel.uiState.collectAsState()
  Surface(
      modifier =
          Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("agendaScreen"),
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      CalendarWidget(
          days = getDaysOfWeekLabels(),
          yearMonth = uiState.yearMonth,
          dates = uiState.dates,
          onPreviousMonthButtonClicked = { prevMonth ->
            agendaViewModel.toPreviousMonth(prevMonth)
          },
          onNextMonthButtonClicked = { nextMonth -> agendaViewModel.toNextMonth(nextMonth) },
          onDateClickListener = { date -> agendaViewModel.onDateSelected(date) })
      Spacer(modifier = Modifier.padding(1.dp))
      HorizontalDivider(
          modifier = Modifier.padding(horizontal = 12.dp),
          thickness = 1.dp,
          color = MaterialTheme.colorScheme.secondary)
      // Implement the daily agenda here
    }
  }
}

/**
 * A Composable that lays out the calendar widget, including the header with the current month and
 * navigation buttons, the day of the week labels, and the grid of dates that users can select.
 *
 * @param days An array of strings representing the days of the week.
 * @param yearMonth The current year and month being displayed.
 * @param dates A list of `CalendarUiState.Date` objects representing the dates to be displayed in
 *   the current month.
 * @param onPreviousMonthButtonClicked A lambda function to be called when the user requests to
 *   navigate to the previous month.
 * @param onNextMonthButtonClicked A lambda function to be called when the user requests to navigate
 *   to the next month.
 * @param onDateClickListener A lambda function to be called when the user selects a date.
 */
@Composable
fun CalendarWidget(
    days: Array<String>,
    yearMonth: YearMonth,
    dates: List<CalendarUiState.Date>,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row {
      repeat(days.size) {
        val item = days[it]
        DayItem(item, modifier = Modifier.weight(1f))
      }
    }
    Header(
        yearMonth = yearMonth,
        onPreviousMonthButtonClicked = onPreviousMonthButtonClicked,
        onNextMonthButtonClicked = onNextMonthButtonClicked)
    Content(dates = dates, onDateClickListener = onDateClickListener)
  }
}

/**
 * Composable function that represents the header of the calendar. It displays the current month and
 * year and provides buttons to navigate to the previous or next month.
 *
 * @param yearMonth The current year and month being displayed.
 * @param onPreviousMonthButtonClicked A lambda function to be invoked when the previous month
 *   navigation button is clicked.
 * @param onNextMonthButtonClicked A lambda function to be invoked when the next month navigation
 *   button is clicked.
 */
@Composable
fun Header(
    yearMonth: YearMonth,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
) {
  Row {
    IconButton(onClick = { onPreviousMonthButtonClicked.invoke(yearMonth.minusMonths(1)) }) {
      Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
          contentDescription = stringResource(id = R.string.back))
    }
    Text(
        text = yearMonth.getDisplayName(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1f).align(Alignment.CenterVertically))
    IconButton(onClick = { onNextMonthButtonClicked.invoke(yearMonth.plusMonths(1)) }) {
      Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
          contentDescription = stringResource(id = R.string.next))
    }
  }
}

/**
 * Composable function that represents a single day item in the calendar. It displays a label for
 * the day of the week.
 *
 * @param day A string representing the day of the week.
 * @param modifier A `Modifier` to be applied to this Composable for styling and layout purposes.
 */
@Composable
fun DayItem(day: String, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    Text(
        text = day,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.align(Alignment.Center).padding(10.dp))
  }
}

/**
 * Composable function that represents the content area of the calendar, arranging the date items in
 * a grid-like manner.
 *
 * @param dates A list of `CalendarUiState.Date` objects to be displayed as date items.
 * @param onDateClickListener A lambda function to be called when a date item is clicked.
 */
@Composable
fun Content(
    dates: List<CalendarUiState.Date>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
  Column {
    var index = 0
    repeat(MAX_ROWS_CALENDAR) {
      if (index >= dates.size) return@repeat
      Row {
        repeat(DAYS_IN_A_WEEK) {
          val item = if (index < dates.size) dates[index] else CalendarUiState.Date.Empty
          ContentItem(
              date = item, onClickListener = onDateClickListener, modifier = Modifier.weight(1f))
          index++
        }
      }
    }
  }
}

/**
 * Composable function that represents a single date item in the calendar. It displays the date and
 * indicates whether it is selected.
 *
 * @param date The `CalendarUiState.Date` object representing the date to be displayed.
 * @param onClickListener A lambda function to be called when this date item is clicked.
 * @param modifier A `Modifier` to be applied to this Composable for styling and layout purposes.
 */
@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier
) {
  // Define the content description
  val description =
      "Date ${date.dayOfMonth}, ${if (date.isSelected) "Selected" else "Not Selected"}"

  Box(
      modifier =
          modifier
              .aspectRatio(1f) // Makes the box a square to fit a circle perfectly
              .clip(CircleShape) // Clips the Box into a Circle
              .background(
                  color =
                      if (date.isSelected) {
                        MaterialTheme.colorScheme.secondaryContainer
                      } else {
                        Color.Transparent
                      })
              .clickable { onClickListener(date) }
              .semantics {
                contentDescription = description
              } // Add semantics with contentDescription
      ) {
        Text(
            text = date.dayOfMonth,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Center).padding(10.dp))
      }
}
