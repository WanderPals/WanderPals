package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OverviewViewModelTest {

  private lateinit var viewModel: OverviewViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

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
    val factory = OverviewViewModel.OverviewViewModelFactory(mockTripsRepository)
    viewModel = factory.create(OverviewViewModel::class.java)
  }

  private fun setupMockResponses() {
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.getAllTrips() } returns
        listOf(Trip("trip1", "description", LocalDate.now(), LocalDate.now(), 100.0, "location"))
    coEvery { mockTripsRepository.addTrip(any()) } returns true
    coEvery { mockTripsRepository.addTripId(any()) } returns true
    coEvery { mockTripsRepository.getTrip(any()) } returns
        Trip("trip1", "description", LocalDate.now(), LocalDate.now(), 100.0, "location")
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `getAllTrips fetches trips successfully and updates state`() =
      runBlockingTest(testDispatcher) {
        viewModel.getAllTrips()

        // Wait for all coroutines started during the test to complete
        advanceUntilIdle()

        // Assert that the state reflects the expected list of trips
        assertEquals(
            listOf(
                Trip("trip1", "description", LocalDate.now(), LocalDate.now(), 100.0, "location")),
            viewModel.state.value)
        assertFalse(viewModel.isLoading.value)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `createTrip adds a trip and updates state`() =
      runBlockingTest(testDispatcher) {
        val newTrip =
            Trip("trip2", "description", LocalDate.now(), LocalDate.now(), 200.0, "location")

        // Mock the behavior of getAllTrips to include the new trip after it is created
        coEvery { mockTripsRepository.getAllTrips() } returns
            listOf(
                Trip("trip1", "description", LocalDate.now(), LocalDate.now(), 100.0, "location"),
                newTrip)

        // Act by creating a new trip and refreshing the trip list
        viewModel.createTrip(newTrip)
        viewModel.getAllTrips()

        // Ensure coroutine effects are applied
        advanceUntilIdle()

        // Assert that the state now includes both trips
        assertEquals(
            listOf(
                Trip("trip1", "description", LocalDate.now(), LocalDate.now(), 100.0, "location"),
                newTrip),
            viewModel.state.value)
      }

  @Test
  fun `joinTrip adds user to trip successfully`() = runBlocking {
    val tripId = "tripId"
    coEvery { mockTripsRepository.getTrip(tripId) } returns
        Trip("", "", LocalDate.now(), LocalDate.now(), 0.0, "")
    val success = viewModel.joinTrip(tripId)
    assertTrue(success)
  }

  @Test
  fun `addUserToSend updates user to send when email is found`() = runBlocking {
    val user = "user"
    val email = "email@example.com"
    coEvery { mockTripsRepository.getUserEmail(user) } returns email
    viewModel.addUserToSend(user)
    assertEquals(email, viewModel.userToSend.value)
    assertTrue(viewModel.canSend.value)
  }

  @Test
  fun `clearUserToSend clears the user to send data`() = runBlocking {
    viewModel.clearUserToSend()
    assertEquals("", viewModel.userToSend.value)
    assertFalse(viewModel.canSend.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    // Reset the main dispatcher to the original Main dispatcher after tests
    Dispatchers.resetMain()
  }
}
