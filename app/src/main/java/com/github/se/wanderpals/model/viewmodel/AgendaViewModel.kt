package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarDataSource
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the agenda of a trip, handling UI state and data interactions for a
 * calendar view.
 *
 * @property tripId The identifier of the trip for which the agenda is being managed.
 */
open class AgendaViewModel(tripId: String, private val tripsRepository: TripsRepository?) :
    ViewModel() {

  /** Lazily initialized data source for calendar data. */
  private val dataSource by lazy { CalendarDataSource() }

  /** Private mutable state flow for the UI state of the agenda. */
  private val _uiState = MutableStateFlow(CalendarUiState.Init)

  /** Exposed read-only state flow of the UI state. */
  open var uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

  /** Private mutable state flow for the daily activities of the selected date. */
  private val _dailyActivities = MutableStateFlow<List<Stop>>(emptyList())

  /** Exposed read-only state flow of the daily activities. */
  val dailyActivities: StateFlow<List<Stop>> = _dailyActivities.asStateFlow()

  init {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(dates = dataSource.getDates(currentState.yearMonth, LocalDate.now()))
      }
    }
  }

  /**
   * Handles date selection, toggling its selection status and updating the UI state accordingly.
   *
   * @param selectedDate The date selected by the user.
   */
  fun onDateSelected(selectedDate: CalendarUiState.Date) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        // Convert CalendarUiState.Date to LocalDate
        val selectedLocalDate =
            LocalDate.of(
                selectedDate.year.value,
                selectedDate.yearMonth.month,
                selectedDate.dayOfMonth.toInt())

        val newSelectedDate =
            if (currentState.selectedDate == selectedLocalDate) null
            else selectedLocalDate // Toggle selection

        // Mark the selected date and update the list
        val updatedDates =
            currentState.dates.map { date ->
              if (date.dayOfMonth == selectedDate.dayOfMonth) {
                // Toggle the selection status of the date
                selectedDate.copy(isSelected = !selectedDate.isSelected)
              } else {
                // If it's not the selected date, ensure it's not marked as selected
                if (date.isSelected) date.copy(isSelected = false) else date
              }
            }
        currentState.copy(dates = updatedDates, selectedDate = newSelectedDate)
      }
    }
  }

  /**
   * Updates the UI state to show the next month's dates.
   *
   * @param nextMonth The next month to display.
   */
  fun toNextMonth(nextMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(
            yearMonth = nextMonth,
            dates = dataSource.getDates(nextMonth, currentState.selectedDate))
      }
    }
  }

  /**
   * Updates the UI state to show the previous month's dates.
   *
   * @param prevMonth The previous month to display.
   */
  fun toPreviousMonth(prevMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(
            yearMonth = prevMonth,
            dates = dataSource.getDates(prevMonth, currentState.selectedDate))
      }
    }
  }

  /** Fetches the daily activities for the selected date. */
  private suspend fun getDailyActivities(selectedDate: LocalDate): List<Stop>? {
    // Assuming tripsRepository.getAllStopsFromTrip returns a List<Stop>
    val allStops = tripsRepository?.getAllStopsFromTrip("tripId")

    // Filter stops to include only those that occur on the selected date
    val dailyActivities = allStops?.filter { stop -> stop.date.isEqual(selectedDate) }

    return dailyActivities
  }

  fun fetchDailyActivities(selectedDate: LocalDate) {
    viewModelScope.launch {
      _dailyActivities.value = getDailyActivities(selectedDate)?.toList() ?: emptyList()
    }
  }
}
