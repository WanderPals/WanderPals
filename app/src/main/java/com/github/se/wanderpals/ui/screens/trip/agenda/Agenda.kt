package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.LocalDate
import java.time.YearMonth

private const val DAYS_IN_A_WEEK = 7
private const val MAX_ROWS_CALENDAR = 6

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
  val dailyActivities by agendaViewModel.dailyActivities.collectAsState()

  var isDrawerExpanded by remember { mutableStateOf(false) }

  var isStopPressed by remember { mutableStateOf(false) }
  var selectedStopId by remember { mutableStateOf("") }

  Scaffold(
      topBar = {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
          Banner(
              agendaViewModel,
              isDrawerExpanded,
              onToggle = { isDrawerExpanded = !isDrawerExpanded })
          AnimatedVisibility(
              visible = isDrawerExpanded,
              modifier =
                  Modifier.background(color = MaterialTheme.colorScheme.surface).fillMaxWidth()) {
                CalendarWidget(
                    days = getDaysOfWeekLabels(),
                    yearMonth = uiState.yearMonth,
                    dates = uiState.dates,
                    onPreviousMonthButtonClicked = { prevMonth ->
                      agendaViewModel.toPreviousMonth(prevMonth)
                    },
                    onNextMonthButtonClicked = { nextMonth ->
                      agendaViewModel.toNextMonth(nextMonth)
                    },
                    onDateClickListener = { date -> agendaViewModel.onDateSelected(date) })
              }
          Spacer(modifier = Modifier.padding(1.dp))
        }
      },
      modifier = Modifier.fillMaxSize().testTag("agendaScreen"),
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
      if (isDrawerExpanded) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary)
      }
      // Daily agenda implementation
      DailyActivities(
          agendaViewModel = agendaViewModel,
          onActivityItemClick = { stopId ->
            isStopPressed = true
            selectedStopId = stopId
          })
    }
  }

  if (isStopPressed) {
    val selectedStop = dailyActivities.find { stop -> stop.stopId == selectedStopId }!!
    StopInfoDialog(stop = selectedStop, closeDialogueAction = { isStopPressed = false })
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
  Column(modifier = Modifier.padding(16.dp).background(MaterialTheme.colorScheme.surface)) {
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
 * Composable function that displays the daily activities for a selected date.
 *
 * @param agendaViewModel The view model for managing the agenda of a trip.
 */
@Composable
fun Banner(agendaViewModel: AgendaViewModel, isExpanded: Boolean, onToggle: () -> Unit) {
  val uiState by agendaViewModel.uiState.collectAsState()
  val selectedDate = uiState.selectedDate ?: LocalDate.now()

  Box(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { onToggle() }
              .padding(8.dp)
              .background(MaterialTheme.colorScheme.primary)
              .testTag("Banner")) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          DisplayDate(date = selectedDate, color = MaterialTheme.colorScheme.onPrimary)
          // Optional: Add an icon to indicate the expand/collapse action
          Icon(
              imageVector =
                  if (isExpanded) Icons.Default.KeyboardArrowUp
                  else Icons.Default.KeyboardArrowDown,
              contentDescription = "Toggle",
              tint = MaterialTheme.colorScheme.onPrimary)
          Spacer(modifier = Modifier.weight(1f))
          // Add an icon to tap that opens the full list of all stops for the trip
          IconButton(
              onClick = {
                // Navigate to the stops list screen
                navigationActions.navigateTo(Route.STOPS_LIST)
              },
              modifier = Modifier.testTag("AllStopsButton"),
              content = {
                Icon(
                    painter = painterResource(id = R.drawable.stops_list_icon),
                    contentDescription = "Open Stops",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
              })
        }
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
  // Assuming dayOfMonth is an empty string for empty dates, or add a specific check if possible.
  val isEmptyDate = date.dayOfMonth.isEmpty()

  val baseModifier =
      modifier
          .aspectRatio(1f)
          .clip(CircleShape)
          .background(
              if (!isEmptyDate && date.isSelected) MaterialTheme.colorScheme.secondaryContainer
              else Color.Transparent)
          .semantics {
            contentDescription =
                if (!isEmptyDate)
                    "Date ${date.dayOfMonth}, ${if (date.isSelected) "Selected" else "Not Selected"}"
                else "Empty Date Cell"
          }

  // Apply clickable only if the date is not empty.
  val finalModifier =
      if (!isEmptyDate) {
        baseModifier.clickable { onClickListener(date) }
      } else baseModifier

  Box(modifier = finalModifier) {
    if (!isEmptyDate) {
      Text(
          text = date.dayOfMonth,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.align(Alignment.Center).padding(10.dp))
    }
  }
}
