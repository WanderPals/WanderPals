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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
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
 *     @param tripId The unique identifier of the trip for which the agenda is being displayed.
 *     @param tripsRepository The repository for accessing trips data.
 */
@Composable
fun Agenda(agendaViewModel: AgendaViewModel, tripId: String, tripsRepository: TripsRepository) {
  val uiState by agendaViewModel.uiState.collectAsState()
  val trip by agendaViewModel.trip.collectAsState()

  var isDrawerExpanded by remember { mutableStateOf(false) }

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
                    onDateClickListener = { date -> agendaViewModel.onDateSelected(date) },
                    trip = trip)
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
          agendaViewModel = agendaViewModel, tripId = tripId, tripsRepository = tripsRepository)
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
 * @param trip The trip for which the agenda is being displayed.
 */
@Composable
fun CalendarWidget(
    days: Array<String>,
    yearMonth: YearMonth,
    dates: List<CalendarUiState.Date>,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
    trip: Trip
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
    Content(dates = dates, onDateClickListener = onDateClickListener, trip = trip)
  }
}

/**
 * Composable function that displays the daily activities for a selected date.
 *
 * @param agendaViewModel The view model for managing the agenda of a trip.
 * @param isExpanded A boolean value indicating whether the daily activities are expanded.
 * @param onToggle A lambda function to be invoked when the daily activities are expanded or
 *   collapsed.
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
 * @param trip The trip for which the agenda is being displayed.
 */
@Composable
fun Content(
    dates: List<CalendarUiState.Date>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
    trip: Trip
) {
  Column {
    var index = 0
    repeat(MAX_ROWS_CALENDAR) {
      if (index >= dates.size) return@repeat
      Row {
        repeat(DAYS_IN_A_WEEK) {
          val item = if (index < dates.size) dates[index] else CalendarUiState.Date.Empty
          val isWithinTrip = isWithinTripRange(item, trip.startDate, trip.endDate)

          ContentItem(
              date = item,
              onClickListener = onDateClickListener,
              modifier = Modifier.weight(1f),
              isWithinTrip = isWithinTrip,
              isStartDate =
                  item.dayOfMonth == trip.startDate.dayOfMonth.toString() &&
                      item.yearMonth == YearMonth.from(trip.startDate),
              isEndDate =
                  item.dayOfMonth == trip.endDate.dayOfMonth.toString() &&
                      item.yearMonth == YearMonth.from(trip.endDate),
              isStartOfLine = index % DAYS_IN_A_WEEK == 0,
              isEndOfLine = index % DAYS_IN_A_WEEK == DAYS_IN_A_WEEK - 1)
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
 * @param isWithinTrip A boolean value indicating whether the date is within the trip range.
 * @param isStartDate A boolean value indicating whether the date is the start date of the trip.
 * @param isEndDate A boolean value indicating whether the date is the end date of the trip.
 * @param isStartOfLine A boolean value indicating whether this date item is at the start of a row
 *   of the calendar.
 * @param isEndOfLine A boolean value indicating whether this date item is at the end of a row of
 *   the calendar.
 */
@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier,
    isWithinTrip: Boolean,
    isStartDate: Boolean,
    isEndDate: Boolean,
    isStartOfLine: Boolean,
    isEndOfLine: Boolean
) {
  // Assuming dayOfMonth is an empty string for empty dates, or add a specific check if possible.
  val isEmptyDate = date.dayOfMonth.isEmpty()

  // Set the marker color based on the stop status
  val markerColor =
      when (date.stopStatus) {
        CalendarUiState.StopStatus.CURRENT -> MaterialTheme.colorScheme.tertiary // Stop added
        CalendarUiState.StopStatus.COMING_SOON ->
            MaterialTheme.colorScheme.inversePrimary // Coming soon
        CalendarUiState.StopStatus.PAST -> MaterialTheme.colorScheme.outlineVariant // Past stop
        else -> Color.Transparent // No stop
      }

  val backgroundShape =
      when {
        isStartDate || isStartOfLine -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
        isEndDate || isEndOfLine -> RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
        else -> RoundedCornerShape(0.dp)
      }
  val tripBackgroundModifier =
      Modifier.clip(backgroundShape)
          .background(
              if (isWithinTrip)
                  MaterialTheme.colorScheme.tertiaryContainer // background for trip range
              else Color.Transparent)
          .testTag("DateBackground_${date.dayOfMonth}") // test tag for trip range background

  val dateBackgroundModifier =
      if (!isEmptyDate && date.isSelected) {
        Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer)
      } else Modifier

  // Apply clickable only if the date is not empty.
  val finalModifier =
      modifier
          .aspectRatio(1f)
          .background(Color.Transparent)
          .then(tripBackgroundModifier) // first apply the trip background modifier
          .clickable(enabled = !isEmptyDate) { onClickListener(date) }
          .then(dateBackgroundModifier) // then apply the date background modifier
          .padding(10.dp)
          .semantics {
            contentDescription =
                if (!isEmptyDate)
                    "Date ${date.dayOfMonth}, ${if (date.isSelected) "Selected" else "Not Selected"}"
                else "Empty Date Cell"
          }

  Box(modifier = finalModifier) {
    if (!isEmptyDate) {
      Text(
          text = date.dayOfMonth,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.align(Alignment.TopCenter))
      // Displaying the status dot below the date
      if (date.stopStatus !=
          CalendarUiState.StopStatus
              .NONE) { // I use "not equal to the NONE status" here, because we might have more
        // statuses in the future
        Box(
            modifier =
                Modifier.align(
                        Alignment.BottomCenter) // Center the dot at the bottom of the date cell
                    .size(8.dp) // Size of the dot
                    .clip(CircleShape) // Make it circular
                    .background(markerColor) // Set the appropriate color
                    .padding(bottom = 4.dp) // Add some padding to the bottom
                    .testTag(
                        when (date.stopStatus) {
                          CalendarUiState.StopStatus.CURRENT -> "MarkerCURRENT"
                          CalendarUiState.StopStatus.COMING_SOON -> "MarkerCOMING_SOON"
                          CalendarUiState.StopStatus.PAST -> "MarkerPAST"
                          else -> "MarkerNONE"
                        }))
      }
    }
  }
}

/**
 * the function to determine if the date is within the trip range.
 *
 * @param date The date to be checked.
 * @param startDate The start date of the trip.
 * @param endDate The end date of the trip.
 */
fun isWithinTripRange(
    date: CalendarUiState.Date,
    startDate: LocalDate,
    endDate: LocalDate
): Boolean {
  if (date.dayOfMonth.isEmpty()) return false
  val currentDate = LocalDate.of(date.year.value, date.yearMonth.month, date.dayOfMonth.toInt())
  return currentDate.isAfter(startDate.minusDays(1)) && currentDate.isBefore(endDate.plusDays(1))
}
