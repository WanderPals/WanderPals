package com.github.se.wanderpals.ui.screens.trip.agenda

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyActivitiesTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkSelectedDateIsDisplayed() {
    // Assuming you have a way to inject or use AgendaViewModel within MainActivity
    var testDate = LocalDate.now()
    val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    composeTestRule.setContent { Agenda(testViewModel) }

    composeTestRule.waitForIdle() // Wait for UI to update

    // Format the date as it would appear on the screen
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    val formattedDefaultDate = testDate.format(formatter)

    // Now, use the testTag to find the element
    composeTestRule
        .onNodeWithTag("displayDateText")
        .assertIsDisplayed() // Check if the element is displayed
        .assertTextEquals(
            formattedDefaultDate) // Additionally, check if the text matches the expected date

    // Change the date selected
    testViewModel.onDateSelected(CalendarUiState.Date("15", YearMonth.now(), Year.now(), false))
    testDate = YearMonth.now().atDay(15)
    val formattedSelectedDate = testDate.format(formatter)

    composeTestRule.waitForIdle() // Ensure UI has time to update after state change.

    if (formattedSelectedDate != null) {
      composeTestRule
          .onNodeWithTag("displayDateText")
          .assertIsDisplayed() // Check if the element is displayed
          .assertTextEquals(formattedSelectedDate) // Check if the text matches the expected date
    }
  }
}
