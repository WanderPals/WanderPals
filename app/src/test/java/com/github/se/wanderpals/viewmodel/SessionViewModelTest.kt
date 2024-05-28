package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SessionViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SessionViewModelTest {

  private lateinit var viewModel: SessionViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private lateinit var mockNavigationActions: NavigationActions
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    // Set the main dispatcher to a test dispatcher to control coroutine execution
    Dispatchers.setMain(testDispatcher)

    // Mock the TripsRepository to be used in the ViewModel
    mockTripsRepository = mockk(relaxed = true)
    mockNavigationActions = mockk(relaxed = true)
    setupMockResponses()

    // Initialize necessary managers with mocks or default test values

    // Create the ViewModel using a factory with the mocked repository
    val factory = SessionViewModel.SessionViewModelFactory(mockTripsRepository)
    viewModel = factory.create(SessionViewModel::class.java)
  }

  private fun setupMockResponses() {
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.getUserFromTrip(any(), "") } returns User(role = Role.OWNER)
    coEvery { mockTripsRepository.getUserFromTrip(any(), "user") } returns null
    coEvery { mockTripsRepository.updateUserInTrip(any(), any()) } returns true
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateUserForCurrentUser updates user role successfully`() =
      runBlockingTest(testDispatcher) {
        val tripId = "trip1"
        SessionManager.setUserSession("", "user@example.com", "token", Role.MEMBER)

        viewModel.updateUserForCurrentUser(tripId, mockNavigationActions)
        advanceUntilIdle()
        assertEquals(
            Role.OWNER, SessionManager.getCurrentUser()?.role) // Check if the role is updated
        SessionManager.logout() // Reset the session manager
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateUserForCurrentUser navigates to overview on fail`() =
      runBlockingTest(testDispatcher) {
        val tripId = "trip1"
        SessionManager.setUserSession("user", "user@example.com", "token", Role.MEMBER)

        viewModel.updateUserForCurrentUser(tripId, mockNavigationActions)
        advanceUntilIdle()
        verify {
          mockNavigationActions.navigateTo(Route.OVERVIEW)
        } // Check if the navigation action is called
        SessionManager.logout() // Reset the session manager
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }
}
