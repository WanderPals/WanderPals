package com.github.se.wanderpals.ui.screens.trip

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.Locale

@Preview(showSystemUi = true)
@Composable
fun AgendaPreview() {
  WanderPalsTheme { Agenda("") }
}

/**
 * The main entry point Composable for the Agenda screen. It displays a calendar view that allows
 * users to navigate through months and select dates. This screen is part of the WanderPals
 * application, focusing on trip planning and scheduling.
 *
 * @param tripId The ID of the trip for which the agenda is being displayed. Used to fetch relevant
 *   data for the trip.
 * @param viewModel The ViewModel that holds and manages UI-related data for the Agenda screen. It
 *   defaults to a ViewModel instance provided by the `viewModel()` function.
 */
@Composable
fun Agenda(tripId: String, viewModel: AgendaViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsState()
  Surface(
      modifier =
          Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("agendaScreen"),
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      CalendarWidget(
          days = DateUtil.daysOfWeek,
          yearMonth = uiState.yearMonth,
          dates = uiState.dates,
          onPreviousMonthButtonClicked = { prevMonth -> viewModel.toPreviousMonth(prevMonth) },
          onNextMonthButtonClicked = { nextMonth -> viewModel.toNextMonth(nextMonth) },
          onDateClickListener = { date -> viewModel.onDateSelected(date) })
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
    repeat(6) {
      if (index >= dates.size) return@repeat
      Row {
        repeat(7) {
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

/**
 * Data class representing the UI state of the calendar. It includes the current year and month
 * being displayed, a list of dates for the month, and an optional selected date.
 *
 * @property yearMonth The current year and month being displayed in the calendar.
 * @property dates A list of `Date` objects representing the dates in the current month.
 * @property selectedDate An optional `LocalDate` representing the currently selected date, if any.
 */
data class CalendarUiState(
    val yearMonth: YearMonth,
    val dates: List<Date>,
    val selectedDate: LocalDate? = null
) {
  companion object {
    val Init = CalendarUiState(yearMonth = YearMonth.now(), dates = emptyList())
  }

  /**
   * Data class representing a single date in the calendar. Includes information about the day of
   * the month, the corresponding year and month, and whether the date is selected.
   *
   * @property dayOfMonth The day of the month as a string.
   * @property yearMonth The year and month to which this date belongs.
   * @property year The year as a `Year` object.
   * @property isSelected Boolean indicating whether this date is currently selected.
   */
  data class Date(
      val dayOfMonth: String,
      val yearMonth: YearMonth,
      val year: Year,
      val isSelected: Boolean
  ) {
    companion object {
      // Represents an empty date, used as a placeholder in the calendar grid
      val Empty = Date("", YearMonth.now(), Year.now(), false)
    }
  }
}

/**
 * Class responsible for generating the list of dates for a given month. This includes determining
 * which dates are to be displayed based on the selected date and the current month.
 */
class CalendarDataSource {

  /**
   * Generates a list of `CalendarUiState.Date` objects for a given year and month. This list
   * includes dates from the specified month, marking the selected date if provided.
   *
   * @param yearMonth The year and month for which to generate the date list.
   * @param selectedDate An optional `LocalDate` representing the currently selected date.
   * @return A list of `CalendarUiState.Date` objects representing the dates of the specified month.
   */
  fun getDates(yearMonth: YearMonth, selectedDate: LocalDate?): List<CalendarUiState.Date> {
    return yearMonth.getDayOfMonthStartingFromMonday().map { date ->
      val isSelected = date == selectedDate && date.monthValue == yearMonth.monthValue
      CalendarUiState.Date(
          dayOfMonth = if (date.monthValue == yearMonth.monthValue) "${date.dayOfMonth}" else "",
          yearMonth = yearMonth,
          year = Year.of(date.year),
          isSelected = isSelected)
    }
  }
}

/**
 * A utility object that provides methods related to date operations, such as generating an array of
 * day labels according to the current locale.
 */
object DateUtil {

  /**
   * Generates an array of day labels (e.g., Sun, Mon, Tue) according to the default locale. These
   * labels are used to display the days of the week in the calendar header.
   *
   * @return An array of strings representing the abbreviated day names in the current locale.
   */
  val daysOfWeek: Array<String>
    get() {
      val daysOfWeek = Array(7) { "" }

      for (dayOfWeek in DayOfWeek.values()) {
        val localizedDayName =
            dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
        daysOfWeek[dayOfWeek.value - 1] = localizedDayName
      }

      return daysOfWeek
    }
}

/**
 * Extension function for `YearMonth` class to get a list of `LocalDate` representing all days in
 * the current month starting from the first Monday of the month. This function is useful for
 * arranging the calendar view where weeks start on Monday.
 *
 * @return A list of `LocalDate` objects representing each day of the month starting from the first
 *   Monday, to be used in the calendar grid.
 */
fun YearMonth.getDayOfMonthStartingFromMonday(): List<LocalDate> {
  val firstDayOfMonth = LocalDate.of(year, month, 1)
  val firstMondayOfMonth = firstDayOfMonth.with(DayOfWeek.MONDAY)
  val firstDayOfNextMonth = firstDayOfMonth.plusMonths(1)

  return generateSequence(firstMondayOfMonth) { it.plusDays(1) }
      .takeWhile { it.isBefore(firstDayOfNextMonth) }
      .toList()
}

/**
 * Extension function for `YearMonth` to generate a display name string in the format of "Month
 * Year" (e.g., "March 2024"). This function utilizes the full display name of the month and the
 * year value of the `YearMonth` instance.
 *
 * @return A string representing the full display name of the month and the year (e.g., "March
 *   2024").
 */
fun YearMonth.getDisplayName(): String {
  return "${month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())} $year"
}
