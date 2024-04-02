package com.github.se.wanderpals.ui.screens.trip.agenda

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

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
 * Generates an array of day labels (e.g., Sun, Mon, Tue) according to the default locale. These
 * labels are used to display the days of the week in the calendar header.
 *
 * @return An array of strings representing the abbreviated day names in the current locale.
 */
fun getDaysOfWeekLabels(): Array<String> =
    DayOfWeek.values()
        .map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
        .toTypedArray()

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
  return "${month.getDisplayName(TextStyle.FULL, Locale.getDefault())} $year"
}