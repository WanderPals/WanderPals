package com.github.se.wanderpals.agenda

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.ui.screens.trip.agenda.ActivityItem
import com.github.se.wanderpals.ui.screens.trip.agenda.DailyActivities
import com.github.se.wanderpals.ui.screens.trip.agenda.StopInfoDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyActivitiesTest {

  private val testActivities =
      listOf(
          Stop(
              "1",
              "Title 1",
              "Location 1",
              LocalDate.now(),
              LocalTime.now(),
              0,
              0.0,
              "Description 1",
              GeoCords(1.0, 0.0)),
          Stop(
              "2",
              "Title 2",
              "Location 2",
              LocalDate.now(),
              LocalTime.now(),
              0,
              0.0,
              "Description 2",
              GeoCords(0.0, 1.0)),
          Stop(
              "3",
              "Title 3",
              "Location 3",
              LocalDate.now(),
              LocalTime.now(),
              0,
              0.0,
              "Description 3",
              GeoCords(1.0, 1.0)),
          Stop(
              "4",
              "Title 4",
              "",
              LocalDate.now(),
              LocalTime.now(),
              0,
              0.0,
              "Description 4",
              GeoCords(0.0, 0.0)))
  private val testViewModel = FakeAgendaViewModel(YearMonth.now(), testActivities)
  private val emptyTestViewModel = FakeAgendaViewModel(YearMonth.now(), emptyList())

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkDailyActivitiesAreDisplayed() {

    composeTestRule.setContent {
      DailyActivities(agendaViewModel = testViewModel, onActivityItemClick = {})
    }

    composeTestRule.waitForIdle()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(testActivities[0].stopId).assertIsDisplayed()

    composeTestRule.onNodeWithTag(testActivities[1].stopId).assertIsDisplayed()

    composeTestRule.onNodeWithTag(testActivities[2].stopId).assertIsDisplayed()

    composeTestRule.onNodeWithTag(testActivities[3].stopId).assertIsDisplayed()
  }

  // Check that the content of the activity items is displayed correctly
  @Test
  fun verifyActivityItemsContent() {
    // Set the content once with a composable that includes all test items
    composeTestRule.setContent {
      Column { testActivities.forEach { stop -> ActivityItem(stop = stop, onActivityClick = {}) } }
    }

    // Loop through each test activity and assert its details
    testActivities.forEach { testStop ->

      // Assert that the title is displayed correctly
      composeTestRule
          .onNodeWithTag("ActivityTitle${testStop.stopId}", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextEquals(testStop.title)

      // Prepare the expected time string
      val expectedTime =
          "${testStop.startTime} - ${testStop.startTime.plusMinutes(testStop.duration.toLong())}"

      // Assert that the time is displayed correctly
      composeTestRule
          .onNodeWithTag("ActivityTime${testStop.stopId}", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextEquals(expectedTime)

      val hasLocation = testStop.geoCords.latitude != 0.0 || testStop.geoCords.longitude != 0.0
      // Assert that the address is displayed correctly
      if (hasLocation) {
        composeTestRule
            .onNodeWithTag("ActivityAddress${testStop.stopId}", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals(testStop.address)
      } else {
        composeTestRule
            .onNodeWithTag("ActivityAddress${testStop.stopId}", useUnmergedTree = true)
            .assertIsNotDisplayed()
      }
    }
  }

  @Test
  fun checkNoActivitiesMessageIsDisplayed() {

    composeTestRule.setContent {
      DailyActivities(agendaViewModel = emptyTestViewModel, onActivityItemClick = {})
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("NoActivitiesMessage").assertIsDisplayed()
  }

  @Test
  fun activityDialogDisplayCorrectInfo() {
    composeTestRule.setContent {
      Column {
        var isStopPressed by remember { mutableStateOf(false) }
        var selectedStopId by remember { mutableStateOf("") }

        testActivities.forEach { stop ->
          ActivityItem(
              stop = stop,
              onActivityClick = { stopId ->
                isStopPressed = true
                selectedStopId = stopId
              })
        }
        if (isStopPressed) {
          val selectedStop = testActivities.find { stop -> stop.stopId == selectedStopId }!!
          StopInfoDialog(stop = selectedStop, closeDialogueAction = { isStopPressed = false })
        }
      }
    }
    testActivities.forEach { testStop ->
      composeTestRule.onNodeWithTag("activityItemButton" + testStop.stopId).performClick()
      composeTestRule.onNodeWithTag("activityDialog").assertIsDisplayed()
      composeTestRule.onNodeWithTag("titleText").assertIsDisplayed()
      composeTestRule.onNodeWithTag("titleText").assertTextEquals("Title " + testStop.stopId)
      composeTestRule.onNodeWithTag("activityDescription").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("activityDescription")
          .assertTextEquals("Description " + testStop.stopId)
      composeTestRule.onNodeWithTag("titleDate").assertIsDisplayed()
      composeTestRule.onNodeWithTag("activityDate").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("activityDate")
          .assertTextEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
      composeTestRule.onNodeWithTag("titleSchedule").assertIsDisplayed()
      composeTestRule.onNodeWithTag("activitySchedule").assertIsDisplayed()
      composeTestRule.onNodeWithTag("titleAddress").assertIsDisplayed()
      composeTestRule.onNodeWithTag("activityAddress").assertIsDisplayed()
      if (testStop.address.isNotEmpty()) {
        composeTestRule
            .onNodeWithTag("activityAddress")
            .assertTextEquals("Location " + testStop.stopId)
        composeTestRule.onNodeWithTag("navigationToMapButton" + testStop.stopId).assertIsDisplayed()
      } else {
        composeTestRule.onNodeWithTag("activityAddress").assertTextEquals("No address provided")
        composeTestRule
            .onNodeWithTag("navigationToMapButton" + testStop.stopId)
            .assertIsDisplayed()
            .assertIsNotEnabled()
      }
      composeTestRule.onNodeWithTag("titleBudget").assertIsDisplayed()
      composeTestRule.onNodeWithTag("activityBudget").assertIsDisplayed()
      composeTestRule.onNodeWithTag("activityBudget").assertTextEquals("0.0")
    }
  }
}
