package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.StopItemViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StopItemViewModelTest {

  private lateinit var viewModel: StopItemViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

  private val tripId = "tripId"
  private val stopId = "stopId"

  @Before
  fun setup() {
    // Set the main dispatcher to a test dispatcher to control coroutine execution
    Dispatchers.setMain(testDispatcher)

    // Mock the TripsRepository to be used in the ViewModel
    mockTripsRepository = mockk(relaxed = true)
    setupMockResponses()

    // Create the ViewModel using a factory with the mocked repository
    val factory = StopItemViewModel.StopItemViewModelFactory(mockTripsRepository, tripId)
    viewModel = factory.create(StopItemViewModel::class.java)
  }

  private fun setupMockResponses() {
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.removeStopFromTrip(tripId, stopId) } returns true
  }

  @Test
  fun `deleteStop updates isDeleted state to true`() =
      runBlockingTest(testDispatcher) {
        // Call the deleteStop method
        viewModel.deleteStop(stopId)

        // Wait for all coroutines started during the test to complete
        advanceUntilIdle()

        // Verify that the stop was removed from the trip
        coVerify { mockTripsRepository.removeStopFromTrip(tripId, stopId) }

        // Assert that the isDeleted state was updated to true
        assertTrue(viewModel.isDeleting.value)
      }

  @Test
  fun `resetDeleteState updates isDeleted state to false`() {
    // Set the isDeleted state to true
    viewModel.resetDeleteState()

    // Assert that the isDeleted state was updated to false
    assertFalse(viewModel.isDeleting.value)
  }

  @After
  fun tearDown() {
    // Reset the main dispatcher to the original Main dispatcher after tests
    Dispatchers.resetMain()
  }
}
