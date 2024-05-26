package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Trip
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
open class AgendaViewModel(
  private val tripId: String,
  private val tripsRepository: TripsRepository?
) : ViewModel() {

  /** Lazily initialized data source for calendar data. */
  private val dataSource by lazy { CalendarDataSource() }

  /** Private mutable state flow for the UI state of the agenda. */
  private val _uiState = MutableStateFlow(CalendarUiState.Init)

  /** Exposed read-only state flow of the UI state. */
  open var uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

  /** Private mutable state flow for the daily activities of the selected date. */
  private val _dailyActivities = MutableStateFlow<List<Stop>>(emptyList())

  /** Exposed read-only state flow of the daily activities. */
  open var dailyActivities: StateFlow<List<Stop>> = _dailyActivities.asStateFlow()

  open var selectedDate: LocalDate? = LocalDate.now()

  open val _stopsInfo = MutableStateFlow<Map<LocalDate, CalendarUiState.StopStatus>>(emptyMap())
  private val _trip = MutableStateFlow(Trip(tripId, "", LocalDate.now(), LocalDate.now(), 0.0, ""))
  val trip: StateFlow<Trip> = _trip.asStateFlow()

  /**
   * Initializes the UI state of the agenda by fetching the dates for the current month and updating
   * the UI state accordingly.
   */
  init {
    viewModelScope.launch { // after the data is loaded,
      // the stops info is loaded and the UI state is updated with the dates for the current month,
      // in a synchronous manner.
      loadTripData()

      loadStopsInfo() // Load stops info when ViewModel initializes

      _uiState.update { currentState ->
        currentState.copy(
          dates =
          dataSource.getDates(
            currentState.yearMonth, LocalDate.now(), stopsInfo = _stopsInfo.value))
      }
    }
  }

  private suspend fun loadTripData() {
    tripsRepository?.getTrip(tripId)?.let { trip ->
      _trip.value = trip
    }
  }
  /** Loads the stops information for the trip, marking all existing stops as ADDED. */
  suspend fun loadStopsInfo() { // a suspend function is asynchronous and can be called from
    // a coroutine
    // Fetch all stops related to a specific trip from the repository
    val stops = tripsRepository?.getAllStopsFromTrip(tripId) ?: listOf()

    val today = LocalDate.now()

    // Create a map with the date of each stop and mark it as ADDED
    // Continue if the list is not empty
    if (stops.isNotEmpty()) {
      //      val statusMap =
      //          stops.associateBy(
      //              keySelector = { it.date }, valueTransform = { CalendarUiState.StopStatus.ADDED
      // })
      val statusMap =
        stops.associate { stop ->
          val status =
            when {
              stop.date.isBefore(today) -> CalendarUiState.StopStatus.PAST
              stop.date.isAfter(today) -> CalendarUiState.StopStatus.COMING_SOON
              else -> CalendarUiState.StopStatus.CURRENT
            }
          stop.date to status
        }
      _stopsInfo.value = statusMap
    }
  }

  /**
   * Handles date selection, toggling its selection status and updating the UI state accordingly.
   *
   * @param selectedDate The date selected by the user.
   */
  fun onDateSelected(selectedDate: CalendarUiState.Date) {
    viewModelScope.launch {
      val selectedLocalDate =
        LocalDate.of(
          selectedDate.year.value,
          selectedDate.yearMonth.month,
          selectedDate.dayOfMonth.toInt())

      // Determine if the newly selected date is different from the current state's selected date.
      val isSameAsCurrentSelected = this@AgendaViewModel.selectedDate == selectedLocalDate
      val newSelectedDate = if (isSameAsCurrentSelected) null else selectedLocalDate

      // Update the open var selectedDate
      this@AgendaViewModel.selectedDate = newSelectedDate

      // Now, update the UI state to reflect this change.
      _uiState.update { currentState ->
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
          dates = dataSource.getDates(nextMonth, currentState.selectedDate, _stopsInfo.value))
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
          dates = dataSource.getDates(prevMonth, currentState.selectedDate, _stopsInfo.value))
      }
    }
  }

  /** Fetches the daily activities for the selected date. */
  private suspend fun getDailyActivities(selectedDate: LocalDate): List<Stop>? {
    // Assuming tripsRepository.getAllStopsFromTrip returns a List<Stop>
    val allStops = tripsRepository?.getAllStopsFromTrip(tripId)

    // Filter stops to include only those that occur on the selected date
    return allStops?.filter { stop -> stop.date.isEqual(selectedDate) }
  }

  fun fetchDailyActivities(selectedDate: LocalDate) {
    viewModelScope.launch {
      _dailyActivities.value = getDailyActivities(selectedDate)?.toList() ?: emptyList()
    }
  }

  class AgendaViewModelFactory(
    private val tripId: String,
    private val tripsRepository: TripsRepository
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return AgendaViewModel(tripId, tripsRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}