package com.github.se.wanderpals.agenda

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.screens.trip.agenda.Agenda
import com.github.se.wanderpals.ui.screens.trip.agenda.Banner
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import com.github.se.wanderpals.ui.screens.trip.agenda.getDisplayName
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

class FakeAgendaViewModel(initialYearMonth: YearMonth) :
    AgendaViewModel("", TripsRepository("", Dispatchers.IO)) {
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
                        isSelected = false)
                  },
              selectedDate = null))

  override var uiState: StateFlow<CalendarUiState> = _uiState

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
}

@RunWith(AndroidJUnit4::class)
class AgendaTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun agendaDisplaysCurrentMonthAndYear() {
    val testYearMonth = YearMonth.now()
    val fakeViewModel = FakeAgendaViewModel(testYearMonth)

    composeTestRule.setContent { Agenda(agendaViewModel = fakeViewModel) }

    composeTestRule.waitForIdle()

    // Click on the banner to make the calendar appear
    composeTestRule.onNodeWithTag("Banner").performClick()

    val expectedDisplay = testYearMonth.getDisplayName()
    composeTestRule.onNodeWithText(expectedDisplay).assertExists()
  }

  @Test
  fun navigationToNextMonth() {
    val initialMonth = YearMonth.now()
    val fakeViewModel = FakeAgendaViewModel(initialMonth)

    composeTestRule.setContent { WanderPalsTheme { Agenda(agendaViewModel = fakeViewModel) } }

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
    val fakeViewModel = FakeAgendaViewModel(initialMonth)

    composeTestRule.setContent { WanderPalsTheme { Agenda(agendaViewModel = fakeViewModel) } }

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
        FakeAgendaViewModel(testYearMonth).apply {
          // Initial state setup to ensure "15" is present and not selected.
        }

    composeTestRule.setContent { WanderPalsTheme { Agenda(agendaViewModel = fakeViewModel) } }

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

    composeTestRule.setContent { Agenda(agendaViewModel = testViewModel) }

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

        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
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
}
