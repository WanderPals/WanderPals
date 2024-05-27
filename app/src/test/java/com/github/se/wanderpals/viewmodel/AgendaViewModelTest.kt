package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AgendaViewModel
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AgendaViewModelTest {
  private lateinit var viewModel: AgendaViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    // Set the main dispatcher to the test dispatcher
    Dispatchers.setMain(testDispatcher)

    // Mock the TripsRepository
    mockTripsRepository = mockk(relaxed = true)

    // Create the ViewModel
    viewModel = AgendaViewModel("tripId", mockTripsRepository)
  }

  /**
   * Test the loadTripData method of the AgendaViewModel class to ensure that the trip state is
   * updated correctly.
   */
  @Test
  fun `loadTripData updates trip state correctly`() = runBlockingTest {
    // Prepare mock trip data
    val mockTrip =
        Trip(
            tripId = "tripId",
            title = "Test Trip",
            startDate = LocalDate.of(2024, 5, 15),
            endDate = LocalDate.of(2024, 5, 24),
            totalBudget = 1000.0,
            description = "Test Trip Description")

    coEvery { mockTripsRepository.getTrip("tripId") } returns mockTrip

    // Call the method to test
    viewModel.loadTripData() // Assuming this method is public or internal for testing
    advanceUntilIdle() // Wait for all coroutines to complete

    // Check the resulting state
    assertEquals(mockTrip, viewModel.trip.value)
  }

  /**
   * Test the loadStopsInfo method of the AgendaViewModel class to ensure that the stopsInfo state
   * is updated correctly to PAST. The method should fetch the stops for the trip and update the
   * stopsInfo.
   */
  @Test
  fun `loadStopsInfo updates stopsInfo state`() = runBlockingTest {
    // Prepare mock response
    val mockStops =
        listOf(
            Stop(
                stopId = "stop1",
                title = "Location1",
                address = "Address1",
                date = LocalDate.of(2024, 5, 10), // Properly create LocalDate instances
                startTime = LocalTime.of(10, 0), // Example start time
                duration = 120, // Example duration in minutes
                budget = 50.0, // Example budget
                description = "Description of Location1",
                geoCords =
                    GeoCords(
                        latitude = 40.712776,
                        longitude = -74.005974), // Example coordinates, e.g., for New York City
                website = "http://location1.com", // Example website
                imageUrl = "http://example.com/location1.jpg" // Example image URL
                ),
            Stop(
                stopId = "stop2",
                title = "Location2",
                address = "Address2",
                date = LocalDate.of(2024, 5, 11),
                startTime = LocalTime.of(15, 30), // Different example start time
                duration = 90, // Different example duration
                budget = 75.0, // Different budget
                description = "Description of Location2",
                geoCords =
                    GeoCords(
                        latitude = 34.052235,
                        longitude = -118.243683), // Example coordinates, e.g., for Los Angeles
                website = "http://location2.com",
                imageUrl = "http://example.com/location2.jpg"))
    coEvery { mockTripsRepository.getAllStopsFromTrip(any()) } returns mockStops

    // Call the method to test
    viewModel.loadStopsInfo() // Assuming this method is public or internal for testing
    advanceUntilIdle() // Wait for all coroutines to complete

    // Check the resulting state
    assertEquals(2, viewModel._stopsInfo.value.size)
    assertEquals(CalendarUiState.StopStatus.PAST, viewModel._stopsInfo.value[mockStops[0].date])
    assertEquals(CalendarUiState.StopStatus.PAST, viewModel._stopsInfo.value[mockStops[1].date])
  }

  /**
   * Test the loadStopsInfo method of the AgendaViewModel class to ensure that the stopsInfo state
   * is updated correctly to different statuses.
   */
  @Test
  fun `loadStopsInfo updates stopsInfo state with different statuses`() = runTest {
    // Prepare mock response
    val today = LocalDate.now()
    val mockStops =
        listOf(
            Stop(
                stopId = "stop1",
                title = "Location1",
                address = "Address1",
                date = today,
                startTime = LocalTime.of(10, 0),
                duration = 120,
                budget = 50.0,
                description = "Description of Location1",
                geoCords = GeoCords(40.712776, -74.005974),
                website = "http://location1.com",
                imageUrl = "http://example.com/location1.jpg"),
            Stop(
                stopId = "stop2",
                title = "Location2",
                address = "Address2",
                date = today.plusDays(1),
                startTime = LocalTime.of(15, 30),
                duration = 90,
                budget = 75.0,
                description = "Description of Location2",
                geoCords = GeoCords(34.052235, -118.243683),
                website = "http://location2.com",
                imageUrl = "http://example.com/location2.jpg"),
            Stop(
                stopId = "stop3",
                title = "Location3",
                address = "Address3",
                date = today.minusDays(1),
                startTime = LocalTime.of(9, 0),
                duration = 60,
                budget = 30.0,
                description = "Description of Location3",
                geoCords = GeoCords(51.507351, -0.127758),
                website = "http://location3.com",
                imageUrl = "http://example.com/location3.jpg"))

    coEvery { mockTripsRepository.getAllStopsFromTrip(any()) } returns mockStops

    // Call the method to test
    viewModel.loadStopsInfo() // Assuming this method is public or internal for testing
    advanceUntilIdle() // Wait for all coroutines to complete

    // Check the resulting state
    assertEquals(3, viewModel._stopsInfo.value.size)
    assertEquals(CalendarUiState.StopStatus.CURRENT, viewModel._stopsInfo.value[mockStops[0].date])
    assertEquals(
        CalendarUiState.StopStatus.COMING_SOON, viewModel._stopsInfo.value[mockStops[1].date])
    assertEquals(CalendarUiState.StopStatus.PAST, viewModel._stopsInfo.value[mockStops[2].date])
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }
}
