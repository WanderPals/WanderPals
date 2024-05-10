package com.github.se.wanderpals.stops

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.viewmodel.StopsListViewModel
import com.github.se.wanderpals.ui.screens.trip.stops.StopsList
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Stop values for testing
val stop1 = Stop("stopId1", "stop1", "stop1 address", LocalDate.of(2024, 5, 10))
val stop2 = Stop("stopId2", "stop2", "stop2 address", LocalDate.of(2024, 5, 11))
val stop3 = Stop("stopId3", "stop3", "stop3 address", LocalDate.of(2024, 5, 12))
val stop4 = Stop("stopId4", "stop4", "stop4 address", LocalDate.of(2024, 5, 13))

// Fake ViewModel for testing
class FakeStopsListViewModel : StopsListViewModel(mockk(relaxed = true)) {
  private val _stops = MutableStateFlow(emptyList<Stop>())
  override var stops: StateFlow<List<Stop>> = _stops.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  var allowDataLoading = true

  override fun loadStops(tripId: String) {
    if (allowDataLoading) {
      viewModelScope.launch {
        _isLoading.value = true
        _stops.value = listOf(stop1, stop2, stop3, stop4)
        _isLoading.value = false
      }
    }
  }

  fun clearStops() {
    _stops.value = emptyList()
  }
}

@RunWith(AndroidJUnit4::class)
class StopsListTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var stopsListViewModel: StopsListViewModel

  @Before
  fun setupTest() {
    // Initialize or reset the ViewModel before each test
    stopsListViewModel = FakeStopsListViewModel()
    // Ensure we start with a clean state
    (stopsListViewModel as FakeStopsListViewModel).clearStops()
    (stopsListViewModel as FakeStopsListViewModel).allowDataLoading = false
  }

  @After
  fun tearDownTest() {
    // Optional: Additional cleanup after tests if necessary
    (stopsListViewModel as FakeStopsListViewModel).clearStops()
    (stopsListViewModel as FakeStopsListViewModel).allowDataLoading = false
  }

  private fun setupEnvironment(useEmptyList: Boolean) {
    // Load stops accordingly
    if (!useEmptyList) {
      (stopsListViewModel as FakeStopsListViewModel).allowDataLoading = true
      stopsListViewModel.loadStops("tripId")
    }

    // Set up Compose UI content
    composeTestRule.setContent { StopsList(stopsListViewModel, "tripId") }
  }

  @Test
  fun topBarTitleIsDisplayed() {
    setupEnvironment(useEmptyList = false)
    composeTestRule.onNodeWithTag("StopsListTitle").assertIsDisplayed()
  }

  @Test
  fun backButtonIsDisplayed() {
    setupEnvironment(useEmptyList = false)
    composeTestRule.onNodeWithTag("BackButton").assertIsDisplayed()
  }

  @Test
  fun stopsListIsDisplayed() {
    setupEnvironment(useEmptyList = false)
    // Wait for the content to be loaded
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("stop1").assertIsDisplayed()
    composeTestRule.onNodeWithText("stop2").assertIsDisplayed()
    composeTestRule.onNodeWithText("stop3").assertIsDisplayed()
    composeTestRule.onNodeWithText("stop4").assertIsDisplayed()
  }

  @Test
  fun stopsListIsEmpty() {
    setupEnvironment(useEmptyList = true)
    // Wait for the content to be loaded
    composeTestRule.waitForIdle()
    // Check if the message is displayed
    composeTestRule.onNodeWithText("No stops for this trip").assertIsDisplayed()
    // Check the refresh button is displayed
    composeTestRule.onNodeWithTag("RefreshButton").assertIsDisplayed()
  }

  @Test
  fun refreshButtonCallsLoadStops() {
    setupEnvironment(useEmptyList = true)
    composeTestRule.waitForIdle()

    // Enable data loading and then perform click
    (stopsListViewModel as FakeStopsListViewModel).allowDataLoading = true

    // Perform the click on the refresh button
    composeTestRule.onNodeWithTag("RefreshButton").performClick()

    // Wait for potential asynchronous updates
    composeTestRule.waitForIdle()

    // Check that now the stops are displayed
    composeTestRule.onNodeWithText("stop1").assertIsDisplayed()
    composeTestRule.onNodeWithText("stop2").assertIsDisplayed()
    composeTestRule.onNodeWithText("stop3").assertIsDisplayed()
    composeTestRule.onNodeWithText("stop4").assertIsDisplayed()
  }

  private fun ComposeTestRule.waitForNodeToAppear(tag: String, timeoutMillis: Long = 5000) {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < timeoutMillis) {
      try {
        onNodeWithTag(tag, useUnmergedTree = true).assertIsDisplayed()
        return // Node is displayed, test can proceed
      } catch (e: AssertionError) {
        // Node not displayed yet, wait a bit before trying again
        runBlocking { delay(100) }
      }
    }
    fail("Node with tag '$tag' was not displayed within $timeoutMillis milliseconds.")
  }

  @Test
  fun testStopInfoDialogIsDisplayed() {
    setupEnvironment(useEmptyList = false)
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("stop1").performClick()
    composeTestRule.waitForNodeToAppear("activityDialog")
    composeTestRule.onNodeWithTag("activityDialog", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun checkDatesAreDisplayed() {
    setupEnvironment(useEmptyList = false)
    composeTestRule.waitForIdle()
    composeTestRule.onAllNodesWithTag("DateBox").assertCountEquals(4)
  }
}
