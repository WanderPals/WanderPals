package com.github.se.wanderpals.viewmodel

import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
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

class NotificationsViewModelTest {

  private lateinit var viewModel: NotificationsViewModel
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

    // Create the ViewModel using a factory with the mocked repository
    val factory =
        NotificationsViewModel.NotificationsViewModelFactory(mockTripsRepository, "tripId")
    viewModel = factory.create(NotificationsViewModel::class.java)
  }

  private fun setupMockResponses() {
    // Define responses from the mocked repository to be returned when methods are called
    coEvery { mockTripsRepository.getAllAnnouncementsFromTrip(any()) } returns
        listOf(
            Announcement(
                "Ann2", "Update", "Here's an update", "Admin", "tripId", LocalDateTime.now()))
    coEvery { mockTripsRepository.updateUserInTrip(any(), any()) } returns true
    coEvery { mockTripsRepository.getNotificationList(any()) } returns
        listOf(TripNotification("Welcome to the trip", "", LocalDateTime.now(), ""))
    coEvery { mockTripsRepository.getSuggestionFromTrip(any(), any()) } returns Suggestion()
    coEvery { mockTripsRepository.removeAnnouncementFromTrip(any(), any()) } returns true
    coEvery { mockTripsRepository.addAnnouncementToTrip(any(), any()) } returns true
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `Loading announcements and notifications updates state correctly`() =
      runBlockingTest(testDispatcher) {
        viewModel.updateStateLists()

        advanceUntilIdle()

        assertEquals(1, viewModel.announcementStateList.value.size)
        assertEquals(1, viewModel.notifStateList.value.size)
        assertEquals("Here's an update", viewModel.announcementStateList.value.first().title)
        assertEquals("Welcome to the trip", viewModel.notifStateList.value.first().title)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `Add announcement calls repository method`() =
      runBlockingTest(testDispatcher) {
        val newAnnouncement =
            Announcement(
                "Ann2", "Update", "Here's an update", "Admin", "tripId", LocalDateTime.now())
        viewModel.addAnnouncement(newAnnouncement)

        // Check that correct mocked method was called
        coVerify { mockTripsRepository.addAnnouncementToTrip("tripId", newAnnouncement) }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `Remove announcement calls repository method`() =
      runBlockingTest(testDispatcher) {
        viewModel.removeAnnouncement("Ann1")
        advanceUntilIdle()

        coVerify { mockTripsRepository.removeAnnouncementFromTrip("tripId", "Ann1") }
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
  }
}
