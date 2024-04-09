package com.github.se.wanderpals.agenda

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.screens.trip.agenda.Banner
import com.github.se.wanderpals.ui.screens.trip.agenda.DailyActivities
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyActivitiesTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkDateIsDisplayed() {
    // Assuming you have a way to inject or use AgendaViewModel within MainActivity
    val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    composeTestRule.setContent { Banner(testViewModel, isExpanded = true, onToggle = {}) }

    composeTestRule.waitForIdle() // Wait for UI to update

    composeTestRule.onNodeWithTag("displayDateText", useUnmergedTree = true).assertIsDisplayed()

    /*
    // Format the date as it would appear on the screen
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    val formattedDefaultDate = testDate.format(formatter)

    // Now, use the testTag to find the element
    composeTestRule
        .onNodeWithTag("Banner")
        .assertHasClickAction()
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
          .onNodeWithTag("Banner")
          .assertIsDisplayed() // Check if the element is displayed
          .assertTextEquals(formattedSelectedDate) // Check if the text matches the expected date
    }
     */
  }

  @Test
  fun checkDailyActivitiesAreDisplayed() {
    val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    composeTestRule.setContent { DailyActivities(agendaViewModel = testViewModel) }

    composeTestRule.waitForIdle()

    // Assuming you have a way to inject or use AgendaViewModel within MainActivity
    val testActivities =
        listOf(
            Stop(
                "Activity 1",
                "Description 1",
                "Location 1",
                LocalDate.now(),
                LocalTime.now(),
                0,
                0.0,
                "",
                GeoCords(0.0, 0.0)),
            Stop(
                "Activity 2",
                "Description 2",
                "Location 2",
                LocalDate.now(),
                LocalTime.now(),
                0,
                0.0,
                "",
                GeoCords(0.0, 0.0)),
            Stop(
                "Activity 3",
                "Description 3",
                "Location 3",
                LocalDate.now(),
                LocalTime.now(),
                0,
                0.0,
                "",
                GeoCords(0.0, 0.0)))

    testViewModel._dailyActivities.value = testActivities

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(testActivities[0].stopId).assertIsDisplayed()

    composeTestRule.onNodeWithTag(testActivities[1].stopId).assertIsDisplayed()

    composeTestRule.onNodeWithTag(testActivities[2].stopId).assertIsDisplayed()
  }

  @Test
  fun checkNoActivitiesMessageIsDisplayed() {
    val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    composeTestRule.setContent { DailyActivities(agendaViewModel = testViewModel) }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("NoActivitiesMessage").assertIsDisplayed()
  }
}
