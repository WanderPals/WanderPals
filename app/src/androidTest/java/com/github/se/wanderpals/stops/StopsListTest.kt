package com.github.se.wanderpals.stops

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.StopsListViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.trip.stops.StopsList
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

// Override the tripsRepository with a mock
private class MockTripsRepository : TripsRepository("tripId", dispatcher = mockk()) {
    override suspend fun getAllStopsFromTrip(tripId: String): List<Stop> {
        return listOf(
            Stop("stopId1", "stop1", "stop1 address", LocalDate.of(2024, 5, 10)),
            Stop("stopId2", "stop2", "stop2 address", LocalDate.of(2024, 5, 11)),
            Stop("stopId3", "stop3", "stop3 address", LocalDate.of(2024, 5, 12))
        )
    }
}

@RunWith(AndroidJUnit4::class)
class StopsListTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

    @RelaxedMockK lateinit var mockNavActions: NavigationActions
    private lateinit var mockTripsRepository : TripsRepository
    private lateinit var stopsListViewModel: StopsListViewModel
    private val tripId = "tripId"

  @Before
  fun setUp() {
    // Initialize the mockTripsRepository
    mockTripsRepository = MockTripsRepository()

    // Initialize the StopsListViewModel with the mocked dependencies
    stopsListViewModel = StopsListViewModel(mockTripsRepository, tripId)

    // Call the loadStops() function to load the data
    stopsListViewModel.loadStops()

    // Set the content of the ComposeTestRule
    composeTestRule.setContent { StopsList(stopsListViewModel) }

    // Wait for the data to be loaded
    Thread.sleep(1000)
  }

  @Test
  fun topBarTitleIsDisplayed() {
    composeTestRule.onNodeWithText("Stops").assertIsDisplayed()
  }

    @Test
    fun stopsListIsDisplayed() {
        composeTestRule.onNodeWithText("stop1").assertIsDisplayed()
        composeTestRule.onNodeWithText("stop2").assertIsDisplayed()
        composeTestRule.onNodeWithText("stop3").assertIsDisplayed()
    }
}
