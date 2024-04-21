package com.github.se.wanderpals.announcement

import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.AnnouncementViewModel
import com.github.se.wanderpals.service.SessionManager
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.time.LocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AnnouncementViewModelTest {

  private lateinit var viewModel: AnnouncementViewModel
  private lateinit var tripsRepository: TripsRepository

  @Before
  fun testSetup() {
    // Mock the TripsRepository
    tripsRepository = mockk()
    // Mock the SessionManager object
    mockkObject(SessionManager)
    // Initialize the viewModel with the mocked repository
    viewModel = AnnouncementViewModel(tripsRepository)
  }

  /**
   * Test if the deleteAnnouncement function returns true when the user is an admin and the deletion
   * is successful. This test is expected to return true.
   */
  @Test
  fun deleteAnnouncement_returnsTrue_whenUserIsAdminAndDeletionIsSuccessful() = runBlockingTest {
    // Arrange
    val tripId = "trip123"
    val announcementId = "AnnouncementAdminId"
    val announcementAdmin =
        Announcement(
            announcementId = "AnnouncementAdminId",
            userId = "userAdmin",
            title = "Admin Announcement",
            userName = "Admin User",
            description = "This is an admin Announcement",
            timestamp = LocalDateTime.now())

    // Assume the user is an admin
    every { SessionManager.isAdmin() } returns true
    // Assume the repository delete operation is successful
    coEvery { tripsRepository.removeAnnouncementFromTrip(tripId, announcementId) } returns true

    // Act
    val result = viewModel.deleteAnnouncement(tripId, announcementAdmin)

    // Assert
    assertTrue(result)
  }

  /**
   * Test if the deleteAnnouncement function returns false when the user is not an admin. This test
   * is expected to return false.
   */
  @Test
  fun deleteAnnouncement_returnsFalse_whenUserIsNotAdmin() = runBlockingTest {
    // Arrange
    val tripId = "trip123"
    val announcementNotAdmin =
        Announcement(
            announcementId = "AnnouncementNotAdminId",
            userId = "userNotAdmin",
            title = "Regular User Announcement",
            userName = "Regular User",
            description = "",
            timestamp = LocalDateTime.now())

    // Assume the user is not an admin
    every { SessionManager.isAdmin() } returns false

    // Act
    val result = viewModel.deleteAnnouncement(tripId, announcementNotAdmin)

    // Assert
    assertFalse(result)
  }

  /** Clear all mocks after each test to avoid any side effects on other tests. */
  @After
  fun tearDown() {
    clearMocks(SessionManager)
  }
}
