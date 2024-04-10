package com.github.se.wanderpals.agenda

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.screens.trip.agenda.ActivityItem
import com.github.se.wanderpals.ui.screens.trip.agenda.DailyActivities
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyActivitiesTest {

    private val testViewModel = AgendaViewModel("", TripsRepository("", Dispatchers.Main))

    private val testActivities =
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

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkDailyActivitiesAreDisplayed() {

    composeTestRule.setContent { DailyActivities(agendaViewModel = testViewModel, onActivityItemClick = {}) }

    composeTestRule.waitForIdle()

    testViewModel._dailyActivities.value = testActivities

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(testActivities[0].stopId).assertIsDisplayed()

    composeTestRule.onNodeWithTag(testActivities[1].stopId).assertIsDisplayed()

    composeTestRule.onNodeWithTag(testActivities[2].stopId).assertIsDisplayed()
  }

    // Check that the content of the activity items is displayed correctly
    @Test
    fun verifyActivityItemsContent() {
        // Set the content once with a composable that includes all test items
        composeTestRule.setContent {
            Column {
                testActivities.forEach { stop ->
                    ActivityItem(stop = stop, onActivityClick = {})
                }
            }
        }

        // Loop through each test activity and assert its details
        testActivities.forEach { testStop ->

            // Assert that the title is displayed correctly
            composeTestRule
                .onNodeWithTag("ActivityTitle${testStop.stopId}", useUnmergedTree = true)
                .assertIsDisplayed()
                .assertTextEquals(testStop.title)

            // Prepare the expected time string
            val expectedTime = "${testStop.startTime} - ${testStop.startTime.plusMinutes(testStop.duration.toLong())}"

            // Assert that the time is displayed correctly
            composeTestRule
                .onNodeWithTag("ActivityTime${testStop.stopId}", useUnmergedTree = true)
                .assertIsDisplayed()
                .assertTextEquals(expectedTime)

            // Assert that the address is displayed correctly
            composeTestRule
                .onNodeWithTag("ActivityAddress${testStop.stopId}", useUnmergedTree = true)
                .assertIsDisplayed()
                .assertTextEquals(testStop.address)
        }
    }

  @Test
  fun checkNoActivitiesMessageIsDisplayed() {

    composeTestRule.setContent { DailyActivities(agendaViewModel = testViewModel, onActivityItemClick = {}) }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("NoActivitiesMessage").assertIsDisplayed()
  }

}
