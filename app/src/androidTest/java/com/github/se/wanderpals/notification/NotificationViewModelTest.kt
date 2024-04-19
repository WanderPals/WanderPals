package com.github.se.wanderpals.notification

import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.NotificationViewModel
import com.github.se.wanderpals.service.SessionManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*;

@ExperimentalCoroutinesApi
class NotificationViewModelTest {

    private lateinit var viewModel: NotificationViewModel
    private lateinit var tripsRepository: TripsRepository
    private lateinit var sessionManager: SessionManager

    @Before
    fun testSetup() {
        // Mock the TripsRepository
        tripsRepository = mockk()
        // Initialize the viewModel with the mocked repository
        viewModel = NotificationViewModel(tripsRepository)
        // Mock the SessionManager
        sessionManager = mockk()
    }

    @Test
    fun deleteTripNotification_returnsTrue_whenUserIsAdminAndDeletionIsSuccessful() = runBlockingTest {
        // Arrange
        val tripId = "trip123"
        val notificationId = "notification123"
        // Assume the user is an admin
        coEvery { SessionManager.isAdmin() } returns true
        // Assume the repository delete operation is successful
        coEvery { tripsRepository.removeTripNotificationFromTrip(tripId, notificationId) } returns true

        // Act
        val result = viewModel.deleteTripNotification(tripId, notificationId)

        // Assert
        assertTrue(result)
    }

    @Test
    fun deleteTripNotification_returnsFalse_whenUserIsNotAdmin() = runBlockingTest {
        // Arrange
        val tripId = "trip123"
        val notificationId = "notification123"
        // Assume the user is not an admin
        coEvery { SessionManager.isAdmin() } returns false

        // Act
        val result = viewModel.deleteTripNotification(tripId, notificationId)

        // Assert
        assertFalse(result)
    }
}