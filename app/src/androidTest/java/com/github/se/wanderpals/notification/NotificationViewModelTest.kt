package com.github.se.wanderpals.notification

import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.NotificationViewModel
import com.github.se.wanderpals.service.SessionManager
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class NotificationViewModelTest {

  private lateinit var viewModel: NotificationViewModel
  private lateinit var tripsRepository: TripsRepository

  @Before
  fun testSetup() {
    // Mock the TripsRepository
    tripsRepository = mockk()
    // Mock the SessionManager object
    mockkObject(SessionManager)
    // Initialize the viewModel with the mocked repository
    viewModel = NotificationViewModel(tripsRepository)
  }

  /**
   * Test if the deleteTripNotification function returns true when the user is an admin and the
   * deletion is successful. This test is expected to return true.
   */
  @Test
  fun deleteTripNotification_returnsTrue_whenUserIsAdminAndDeletionIsSuccessful() =
      runBlockingTest {
        // Arrange
        val tripId = "trip123"
        val notificationId = "notifAdminId"
        val tripNotificationAdmin = TripNotification(
          notificationId = "notifAdminId",
          userId = "userAdmin",
          title = "Admin Notification",
          userName = "Admin User",
          description = "This is an admin notification",
          timestamp = LocalDateTime.now()
        )

        // Assume the user is an admin
        every { SessionManager.isAdmin() } returns true
        // Assume the repository delete operation is successful
        coEvery { tripsRepository.removeTripNotificationFromTrip(tripId, notificationId) } returns
            true

        // Act
        val result = viewModel.deleteTripNotification(tripId, tripNotificationAdmin)

        // Assert
        assertTrue(result)
      }

  /**
   * Test if the deleteTripNotification function returns false when the user is not an admin. This
   * test is expected to return false.
   */
  @Test
  fun deleteTripNotification_returnsFalse_whenUserIsNotAdmin() = runBlockingTest {
    // Arrange
    val tripId = "trip123"
    val tripNotificationNotAdmin = TripNotification(
      notificationId = "notifNotAdminId",
      userId = "userNotAdmin",
      title = "Regular User Notification",
      userName = "Regular User",
      description = "",
      timestamp = LocalDateTime.now()
    )

    // Assume the user is not an admin
    every { SessionManager.isAdmin() } returns false

    // Act
    val result = viewModel.deleteTripNotification(tripId, tripNotificationNotAdmin)

    // Assert
    assertFalse(result)
  }

  /** Clear all mocks after each test to avoid any side effects on other tests. */
  @After
  fun tearDown() {
    clearMocks(SessionManager)
  }
}
