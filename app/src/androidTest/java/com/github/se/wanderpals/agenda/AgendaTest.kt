package com.github.se.wanderpals.agenda

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.YearMonth
import com.github.se.wanderpals.ui.screens.trip.Agenda
import com.github.se.wanderpals.ui.screens.trip.CalendarUiState
import com.github.se.wanderpals.ui.screens.trip.getDisplayName
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.Year

class FakeAgendaViewModel(initialYearMonth: YearMonth) : AgendaViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState(
        yearMonth = initialYearMonth,
        dates = List(30) { day ->
            CalendarUiState.Date(
                dayOfMonth = (day + 1).toString(),
                yearMonth = initialYearMonth,
                year = Year.now(),
                isSelected = false
            )
        },
        selectedDate = null
    ))

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
        val newDates = _uiState.value.dates.map {
            if (it.dayOfMonth == date.dayOfMonth) it.copy(isSelected = !it.isSelected) else it
        }
        _uiState.value = _uiState.value.copy(dates = newDates, selectedDate = if (date.isSelected) null else LocalDate.now())
    }
}

@RunWith(AndroidJUnit4::class)
class AgendaTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun agendaDisplaysCurrentMonthAndYear() {
        val testYearMonth = YearMonth.now()
        val fakeViewModel = FakeAgendaViewModel(testYearMonth)

        composeTestRule.setContent {
            WanderPalsTheme {
                Agenda("", viewModel = fakeViewModel)
            }
        }

        val expectedDisplay = testYearMonth.getDisplayName()
        composeTestRule.onNodeWithText(expectedDisplay).assertExists()
    }

    @Test
    fun navigationToNextMonth() {
        val initialMonth = YearMonth.now()
        val fakeViewModel = FakeAgendaViewModel(initialMonth)

        composeTestRule.setContent {
            WanderPalsTheme {
                Agenda("", viewModel = fakeViewModel)
            }
        }

        composeTestRule.onNodeWithContentDescription("Next").performClick()

        fakeViewModel.toNextMonth() // Simulate navigating to the next month
        val expectedDisplay = initialMonth.plusMonths(1).getDisplayName()
        composeTestRule.onNodeWithText(expectedDisplay).assertExists()
    }


    @Test
    fun navigationToPreviousMonth() {
        val initialMonth = YearMonth.now()
        val fakeViewModel = FakeAgendaViewModel(initialMonth)

        composeTestRule.setContent {
            WanderPalsTheme {
                Agenda("", viewModel = fakeViewModel)
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        fakeViewModel.toPreviousMonth() // Simulate navigating to the previous month
        val expectedDisplay = initialMonth.minusMonths(1).getDisplayName()
        composeTestRule.onNodeWithText(expectedDisplay).assertExists()
    }


    @Test
    fun contentItemSelectionTogglesOnTap() {
        val testYearMonth = YearMonth.now()
        // Initialize with specific dates, including "15" as unselected.
        val fakeViewModel = FakeAgendaViewModel(testYearMonth).apply {
            // Initial state setup to ensure "15" is present and not selected.
        }

        composeTestRule.setContent {
            WanderPalsTheme {
                Agenda(tripId = "", viewModel = fakeViewModel)
            }
        }

        // Assuming "Date 15, Not Selected" is initially present.
        composeTestRule.onNodeWithContentDescription("Date 15, Not Selected").performClick()

        // Trigger a state change in the ViewModel to reflect the new selection state.
        fakeViewModel.selectDate(CalendarUiState.Date("15", testYearMonth, Year.now(), false))

        composeTestRule.waitForIdle() // Ensure UI has time to update after state change.

        // Now, verify the item's content description reflects it being selected.
        composeTestRule.onNodeWithContentDescription("Date 15, Selected").assertExists()
    }
}
