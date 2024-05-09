package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.MembersViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MembersViewModelTest {

  private lateinit var viewModel: MembersViewModel
  private lateinit var mockTripsRepository: TripsRepository
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher) // Set the main dispatcher to control coroutine execution
    mockTripsRepository = mockk(relaxed = true) // Create a mock of TripsRepository

    // Setup the mock to return a list of users when called
    val users =
        listOf(
            User(userId = "1", name = "John Doe", role = Role.MEMBER),
            User(userId = "2", name = "Jane Doe", role = Role.OWNER))
    coEvery { mockTripsRepository.getAllUsersFromTrip(any()) } returns users

    // Initialize the MembersViewModel with the mocked repository and a test trip ID
    viewModel = MembersViewModel(mockTripsRepository, "trip1")
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `loadMembers loads correctly`() =
      runBlockingTest(testDispatcher) {
        viewModel.loadMembers()

        advanceUntilIdle()

        // Assert that the members list is updated as expected
        assertEquals(2, viewModel.members.value.size) // Check if the list size is correct
        assertEquals("John Doe", viewModel.members.value[0].name) // Check the first member's name
        assertTrue(
            viewModel.members.value.any { it.role == Role.OWNER }) // Ensure there is an OWNER
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }
}
