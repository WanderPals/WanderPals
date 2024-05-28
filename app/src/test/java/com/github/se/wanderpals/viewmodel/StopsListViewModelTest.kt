package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.StopsListViewModel
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

/*
 * Unit tests for the StopsListViewModel class.
 */
class StopsListViewModelTest {

  private lateinit var viewModel: StopsListViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

  private val tripId = "tripId"

  private val stop1 = Stop("stopId1", "stop1", "stop1 address", LocalDate.of(2024, 5, 10))
  private val stop2 = Stop("stopId2", "stop2", "stop2 address", LocalDate.of(2024, 5, 11))
  private val stop3 = Stop("stopId3", "stop3", "stop3 address", LocalDate.of(2024, 5, 12))
  private val stop4 = Stop("stopId4", "stop4", "stop4 address", LocalDate.of(2024, 5, 13))

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    // Set the main dispatcher to a test dispatcher to control coroutine execution
    Dispatchers.setMain(testDispatcher)

    // Mock the TripsRepository to be used in the ViewModel
    mockTripsRepository = mockk(relaxed = true)
    setupMockResponses()

    // Initialize necessary managers with mocks or default test values
    NotificationsManager.initNotificationsManager(mockTripsRepository)
    SessionManager.setUserSession("user", "user@example.com", "token", Role.MEMBER)

    // Create the ViewModel using a factory with the mocked repository
    val factory = StopsListViewModel.StopsListViewModelFactory(mockTripsRepository, tripId)
    viewModel = factory.create(StopsListViewModel::class.java)
  }

  private fun setupMockResponses() {
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.getAllStopsFromTrip(tripId) } returns
        listOf(stop1, stop2, stop3, stop4)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `loadStops fetches stops successfully and updates state`() =
      runBlockingTest(testDispatcher) {
        viewModel.loadStops()

        // Wait for all coroutines started during the test to complete
        advanceUntilIdle()

        // Assert that the state reflects the expected list of stops
        assertEquals(listOf(stop1, stop2, stop3, stop4), viewModel.stops.value)
        assertFalse(viewModel.isLoading.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    // Reset the main dispatcher to the original Main dispatcher after tests
    Dispatchers.resetMain()
  }
}
