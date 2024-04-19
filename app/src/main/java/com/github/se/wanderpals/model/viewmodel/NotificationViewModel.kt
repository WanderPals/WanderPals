package com.github.se.wanderpals.model.viewmodel

import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager

class NotificationViewModel(
    private val tripsRepository: TripsRepository
) {

    /**
     * Attempts to delete a trip notification. Only allows deletion if the current user is an admin.
     *
     * @param tripId The ID of the trip from which the notification is to be deleted.
     * @param notificationId The ID of the notification to be deleted.
     * @return A boolean representing the success of the operation.
     */
    suspend fun deleteTripNotification(tripId: String, notificationId: String): Boolean {
        // Check if the current user has permission to remove items, only admins can delete notifications
        return if (SessionManager.canRemove()) {
            tripsRepository.removeTripNotificationFromTrip(tripId, notificationId)
        } else {
            false
        }
    }
}
