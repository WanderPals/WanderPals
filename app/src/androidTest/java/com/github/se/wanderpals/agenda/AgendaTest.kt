package com.github.se.wanderpals.agenda

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.screens.trip.agenda.Agenda
import com.github.se.wanderpals.ui.screens.trip.agenda.Banner
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarWidget
import com.github.se.wanderpals.ui.screens.trip.agenda.getDisplayName
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeAgendaViewModel(
    initialYearMonth: YearMonth,
    testActivities: List<Stop>,
    private val initialStopsInfo: MutableStateFlow<Map<LocalDate, CalendarUiState.StopStatus>> =
        MutableStateFlow(emptyMap())
) : AgendaViewModel("", TripsRepository("", Dispatchers.IO)) {
  private val _uiState =
      MutableStateFlow(
          CalendarUiState(
              yearMonth = initialYearMonth,
              dates =
                  List(30) { day ->
                    CalendarUiState.Date(
                        dayOfMonth = (day + 1).toString(),
                        yearMonth = initialYearMonth,
                        year = Year.now(),
                        isSelected = false,
                        stopStatus =
                            initialStopsInfo.value.getOrDefault(
                                LocalDate.now().withDayOfMonth(day + 1),
                                CalendarUiState.StopStatus.NONE))
                  },
              selectedDate = null))

  /** Mutable state flow for the stops info. */
  init {
    _stopsInfo.value = initialStopsInfo.value
  }

  override var uiState: StateFlow<CalendarUiState> = _uiState
  override var dailyActivities: StateFlow<List<Stop>> = MutableStateFlow(testActivities)

  fun toNextMonth() {
    val nextMonth = _uiState.value.yearMonth.plusMonths(1)
    _uiState.value = _uiState.value.copy(yearMonth = nextMonth)
  }

  fun toPreviousMonth() {
    val prevMonth = _uiState.value.yearMonth.minusMonths(1)
    _uiState.value = _uiState.value.copy(yearMonth = prevMonth)
  }

  fun selectDate(date: CalendarUiState.Date) {
    val newDates =
        _uiState.value.dates.map {
          if (it.dayOfMonth == date.dayOfMonth) it.copy(isSelected = !it.isSelected) else it
        }
    _uiState.value =
        _uiState.value.copy(
            dates = newDates, selectedDate = if (date.isSelected) null else LocalDate.now())
  }

  /**
   * Simulates a change in the stop status for a given date.
   *
   * @param date The date to update.
   * @param status The new status to assign to the date.
   */
  fun simulateStopStatusChange(date: LocalDate, status: CalendarUiState.StopStatus) {
    initialStopsInfo.value = initialStopsInfo.value.plus(date to status)
    _stopsInfo.value = initialStopsInfo.value
    _uiState.value =
        _uiState.value.copy(
            dates =
                _uiState.value.dates.map {
                  if (it.dayOfMonth.toInt() == date.dayOfMonth) {
                    it.copy(stopStatus = status)
                  } else it
                })
  }
}

@RunWith(AndroidJUnit4::class)
class AgendaTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun agendaDisplaysCurrentMonthAndYear() {
    val testYearMonth = YearMonth.now()
    val fakeViewModel = FakeAgendaViewModel(testYearMonth, emptyList())

    composeTestRule.setContent {
      Agenda(
          agendaViewModel = fakeViewModel,
          tripId = "",
          tripsRepository = TripsRepository("", Dispatchers.IO))
    }

    composeTestRule.waitForIdle()

    // Click on the banner to make the calendar appear
    composeTestRule.onNodeWithTag("Banner").performClick()

    val expectedDisplay = testYearMonth.getDisplayName()
    composeTestRule.onNodeWithText(expectedDisplay).assertExists()
  }

  @Test
  fun navigationToNextMonth() {
    val initialMonth = YearMonth.now()
    val fakeViewModel = FakeAgendaViewModel(initialMonth, emptyList())

    composeTestRule.setContent {
      Agenda(
          agendaViewModel = fakeViewModel,
          tripId = "",
          tripsRepository = TripsRepository("", Dispatchers.IO))
    }

    // Click on the banner to make the calendar appear
    composeTestRule.onNodeWithTag("Banner").performClick()

    composeTestRule.onNodeWithContentDescription("Next").performClick()

    fakeViewModel.toNextMonth() // Simulate navigating to the next month
    val expectedDisplay = initialMonth.plusMonths(1).getDisplayName()
    composeTestRule.onNodeWithText(expectedDisplay).assertExists()
  }

  @Test
  fun navigationToPreviousMonth() {
    val initialMonth = YearMonth.now()
    val fakeViewModel = FakeAgendaViewModel(initialMonth, emptyList())

    composeTestRule.setContent {
      Agenda(
          agendaViewModel = fakeViewModel,
          tripId = "",
          tripsRepository = TripsRepository("", Dispatchers.IO))
    }

    // Click on the banner to make the calendar appear
    composeTestRule.onNodeWithTag("Banner").performClick()

    composeTestRule.onNodeWithContentDescription("Back").performClick()

    fakeViewModel.toPreviousMonth() // Simulate navigating to the previous month
    val expectedDisplay = initialMonth.minusMonths(1).getDisplayName()
    composeTestRule.onNodeWithText(expectedDisplay).assertExists()
  }

  @Test
  fun contentItemSelectionTogglesOnTap() {
    val testYearMonth = YearMonth.now()
    // Initialize with specific dates, including "15" as unselected.
    val fakeViewModel =
        FakeAgendaViewModel(testYearMonth, emptyList()).apply {
          // Initial state setup to ensure "15" is present and not selected.
        }

    composeTestRule.setContent {
      Agenda(
          agendaViewModel = fakeViewModel,
          tripId = "",
          tripsRepository = TripsRepository("", Dispatchers.IO))
    }

    // Click on the banner to make the calendar appear
    composeTestRule.onNodeWithTag("Banner").performClick()

    // Assuming "Date 15, Not Selected" is initially present.
    composeTestRule.onNodeWithContentDescription("Date 15, Not Selected").performClick()

    // Trigger a state change in the ViewModel to reflect the new selection state.
    fakeViewModel.selectDate(CalendarUiState.Date("15", testYearMonth, Year.now(), false))

    composeTestRule.waitForIdle() // Ensure UI has time to update after state change.

    // Now, verify the item's content description reflects it being selected.
    composeTestRule.onNodeWithContentDescription("Date 15, Selected").assertExists()
  }

  @Test
  fun checkBannerIsDisplayed() {
    val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    composeTestRule.setContent {
      Agenda(
          agendaViewModel = testViewModel,
          tripId = "",
          tripsRepository = TripsRepository("", Dispatchers.IO))
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("Banner").assertIsDisplayed()
  }

  // Test to check the date displayed is correct
  @Test
  fun checkDateIsDisplayedCorrectly() {
    val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    composeTestRule.setContent { Banner(testViewModel, isExpanded = true, onToggle = {}) }

    composeTestRule.waitForIdle()

    val testDate = LocalDate.now()

    val formatter =
        java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            .withLocale(Locale.getDefault())

    val formattedDefaultDate = testDate.format(formatter)

    composeTestRule
        .onNodeWithTag("displayDateText", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(formattedDefaultDate)
  }

  fun checkDateIsDisplayed() {
    // Assuming you have a way to inject or use AgendaViewModel within MainActivity
    val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    composeTestRule.setContent { Banner(testViewModel, isExpanded = true, onToggle = {}) }

    composeTestRule.waitForIdle() // Wait for UI to update

    composeTestRule.onNodeWithTag("displayDateText", useUnmergedTree = true).assertIsDisplayed()
  }

  /** Test to verify that the marker with "ADDED" stop status exists and is displayed. */
  @Test
  fun calendarDateWithAddedStatusShowsMarker() {
    val testYearMonth = YearMonth.of(2024, 5)
    val testDate = LocalDate.of(2024, 5, 5)
    val fakeViewModel = FakeAgendaViewModel(testYearMonth, emptyList())

    // Simulate adding a stop with ADDED status on May 5, 2024
    fakeViewModel.simulateStopStatusChange(testDate, CalendarUiState.StopStatus.CURRENT)

    // Set up the environment for the test
    composeTestRule.setContent {
      CalendarWidget(
          days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
          yearMonth = testYearMonth,
          dates =
              listOf(
                  CalendarUiState.Date(
                      "5",
                      testYearMonth.withMonth(5),
                      year = Year.of(2024),
                      isSelected = false,
                      stopStatus = CalendarUiState.StopStatus.CURRENT)),
          onPreviousMonthButtonClicked = {},
          onNextMonthButtonClicked = {},
          onDateClickListener = {},
      )
    }

    // Find the marker and assert it's displayed on the screen because the stop status is "ADDED"
    composeTestRule.onNodeWithTag("MarkerADDED", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("MarkerADDED", useUnmergedTree = true).assertIsDisplayed()
  }

  /**
   * Test to verify that the marker with "ADDED" stop status does not exist and is not displayed.
   */
  @Test
  fun calendarDateWithNoAddedStatusShowsNoMarker() {
    val testYearMonth = YearMonth.of(2024, 5)
    val testDate = LocalDate.of(2024, 5, 6)
    val fakeViewModel = FakeAgendaViewModel(testYearMonth, emptyList())

    // Ensure the status is NONE for the test date
    fakeViewModel.simulateStopStatusChange(testDate, CalendarUiState.StopStatus.NONE)

    // Set up the environment for the test
    composeTestRule.setContent {
      CalendarWidget(
          days = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
          yearMonth = testYearMonth,
          dates =
              listOf(
                  CalendarUiState.Date(
                      "6",
                      testYearMonth.withMonth(5),
                      year = Year.of(2024),
                      isSelected = false,
                      stopStatus = CalendarUiState.StopStatus.NONE)),
          onPreviousMonthButtonClicked = {},
          onNextMonthButtonClicked = {},
          onDateClickListener = {})
    }

    // Assert that the marker with "ADDED" status is not displayed because the stop status is "NONE"
    composeTestRule.onNodeWithTag("MarkerADDED", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("MarkerADDED", useUnmergedTree = true).isNotDisplayed()
  }
}
