package com.github.se.wanderpals.stops

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.screens.trip.stops.StopItem
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StopItemTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private val stop =
      Stop(
          stopId = "1",
          title = "Test Stop",
          startTime = LocalTime.of(9, 0),
          duration = 60,
          date = LocalDate.of(2023, 5, 16),
          address = "123 Test St",
          budget = 100.0,
          description = "This is a test stop.",
          geoCords = GeoCords(37.7749, -122.4194))

  private val tripsRepository = mockk<TripsRepository>(relaxed = true)

  @Test
  fun testStopItemDisplaysCorrectly() = run {
    composeTestRule.setContent {
      StopItem(stop = stop, tripId = "trip1", tripsRepository = tripsRepository, onDelete = {})
    }

    composeTestRule.onNodeWithTag("activityItemButton1", useUnmergedTree = true).assertExists()
    composeTestRule
        .onNodeWithTag("ActivityTitle1", useUnmergedTree = true)
        .assertTextContains("Test Stop")
    composeTestRule
        .onNodeWithTag("ActivityTime1", useUnmergedTree = true)
        .assertTextContains("09:00 - 10:00")
    composeTestRule
        .onNodeWithTag("ActivityAddress1", useUnmergedTree = true)
        .assertTextContains("123 Test St")
  }

  @Test
  fun testDeleteButtonFunctionality() = run {
    // Set the user as an admin
    SessionManager.setUserSession(role = Role.ADMIN)
    var deleteClicked = false
    composeTestRule.setContent {
      StopItem(
          stop = stop,
          tripId = "trip1",
          tripsRepository = tripsRepository,
          onDelete = { deleteClicked = true })
    }

    composeTestRule.onNodeWithTag("deleteButton1", useUnmergedTree = true).performClick()
    // Wait for the dialog to be displayed
    composeTestRule.waitForIdle()
    // Assuming SessionManager.isAdmin() returns true for testing purpose
    composeTestRule
        .onNodeWithTag("confirmDeleteButton1", useUnmergedTree = true)
        .assertExists()
        .performClick()

    composeTestRule.mainClock.advanceTimeBy(500) // Wait for the delete action to complete

    assert(deleteClicked) { "Delete callback was not triggered" }
  }

  @Test
  fun testDeleteButtonFunctionalityNotWorkingOffline() = run {
    // Set the user as an admin
    SessionManager.setIsNetworkAvailable(false)
    SessionManager.setUserSession(role = Role.ADMIN)
    var deleteClicked = false
    composeTestRule.setContent {
      StopItem(
          stop = stop,
          tripId = "trip1",
          tripsRepository = tripsRepository,
          onDelete = { deleteClicked = true })
    }

    composeTestRule.onNodeWithTag("deleteButton1", useUnmergedTree = true).performClick()
    // Wait for the dialog to be displayed
    composeTestRule.waitForIdle()
    // Assuming SessionManager.isAdmin() returns true for testing purpose
    composeTestRule
        .onNodeWithTag("confirmDeleteButton1", useUnmergedTree = true)
        .assertExists()
        .performClick()

    composeTestRule.mainClock.advanceTimeBy(500) // Wait for the delete action to complete

    assert(!deleteClicked) { "Delete callback was triggered" }
  }

  @Test
  fun testDeleteCancelFunctionality() = run {
    // Set the user as an admin
    SessionManager.setUserSession(role = Role.ADMIN)

    var deleteClicked = false
    composeTestRule.setContent {
      StopItem(
          stop = stop,
          tripId = "trip1",
          tripsRepository = tripsRepository,
          onDelete = { deleteClicked = true })
    }
    composeTestRule.onNodeWithTag("deleteButton1", useUnmergedTree = true).performClick()
    // Wait for the dialog to be displayed
    composeTestRule.waitForIdle()

    // Assuming SessionManager.isAdmin() returns true for testing purpose
    composeTestRule
        .onNodeWithTag("cancelDeleteButton1", useUnmergedTree = true)
        .assertExists()
        .performClick()
    composeTestRule.mainClock.advanceTimeBy(500) // Wait for the delete action to complete
    assert(!deleteClicked) { "Delete callback was not triggered" }
  }

  @Test
  fun testStopInfoDialogDisplayed() = run {
    composeTestRule.setContent {
      StopItem(stop = stop, tripId = "trip1", tripsRepository = tripsRepository, onDelete = {})
    }

    composeTestRule.onNodeWithTag("activityItemButton1").performClick()
    composeTestRule.onNodeWithTag("activityDialog").assertExists()
    composeTestRule.onNodeWithTag("titleText").assertTextContains("Test Stop")
    composeTestRule.onNodeWithTag("activityDate").assertTextContains("Tuesday, 16/05/2023")
    composeTestRule.onNodeWithTag("activitySchedule").assertTextContains("09:00 - 10:00")
    composeTestRule.onNodeWithTag("activityAddress").assertTextContains("123 Test St")
    composeTestRule.onNodeWithTag("activityBudget").assertTextContains("100.0")
    composeTestRule.onNodeWithTag("activityDescription").assertTextContains("This is a test stop.")
  }
}
