package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateSuggestionViewModelTest {

  private lateinit var viewModel: CreateSuggestionViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()
  private val tripId = "tripId"

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)

    mockTripsRepository = mockk(relaxed = true)
    setupMockResponses()

    // Create the ViewModel using a factory with the mocked repository
    val factory = CreateSuggestionViewModel.CreateSuggestionViewModelFactory(mockTripsRepository)
    viewModel = factory.create(CreateSuggestionViewModel::class.java)
  }

  private fun setupMockResponses() {
    coEvery { mockTripsRepository.addSuggestionToTrip(any(), any()) } returns true
    coEvery { mockTripsRepository.getAllSuggestionsFromTrip(tripId) } returns
        listOf(Suggestion(suggestionId = "123"))
    coEvery { mockTripsRepository.updateSuggestionInTrip(tripId, any()) } returns true
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `addSuggestion adds a suggestion and sends notification`() =
      runBlockingTest(testDispatcher) {
        val suggestion = Suggestion(suggestionId = "123")

        val result = viewModel.addSuggestion(tripId, suggestion)
        advanceUntilIdle()

        coVerify { mockTripsRepository.addSuggestionToTrip(tripId, suggestion) }
        coVerify { mockTripsRepository.getAllSuggestionsFromTrip(tripId) }
        assertTrue(result)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateSuggestion updates a suggestion correctly`() =
      runBlockingTest(testDispatcher) {
        val suggestion = Suggestion(suggestionId = "123")

        // Call the method to test
        viewModel.updateSuggestion(tripId, suggestion)
        advanceUntilIdle()

        // Verify the method call was made with the expected parameters
        coVerify {
          mockTripsRepository.updateSuggestionInTrip(
              tripId,
              match {
                it.suggestionId == "123" // More conditions can be added here if needed
              })
        }
        assertTrue(viewModel.updateSuggestion(tripId, suggestion))
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }
}
