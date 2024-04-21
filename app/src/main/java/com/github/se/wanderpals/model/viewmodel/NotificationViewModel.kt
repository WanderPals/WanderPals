package com.github.se.wanderpals.model.viewmodel

import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager

class NotificationViewModel(private val tripsRepository: TripsRepository) {

  /**
   * Attempts to delete a trip notification. Only allows deletion if the current user is an admin.
   *
   * @param tripId The ID of the trip from which the notification is to be deleted.
   * @param tripNotification The notification object to be deleted.
   * @return A boolean representing the success of the operation.
   */
  suspend fun deleteTripNotification(tripId: String, tripNotification: Announcement): Boolean {
    // Check if the current user has permission to remove items, only admins can delete
    // notifications
    return if (SessionManager.canRemove(tripNotification.userId)) {
      tripsRepository.removeTripNotificationFromTrip(tripId, tripNotification.announcementId)
    } else {
      false
    }
  }
}
