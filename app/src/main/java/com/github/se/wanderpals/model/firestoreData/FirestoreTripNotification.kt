package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.TripNotification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Firestore-compatible DTO for a TripNotification, simplifying LocalDateTime to String for
 * compatibility and ensuring that all notification details are correctly handled for interactions
 * with Firestore.
 *
 * @param notificationId The unique identifier for the notification.
 * @param userId The identifier of the user or system that posted the notification.
 * @param title A brief title of the notification.
 * @param userName The name of the user or system entity that posted the notification.
 * @param description Detailed description of the notification.
 * @param timestamp The exact date and time when the notification was created, stored as a String.
 */
data class FirestoreTripNotification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val userName: String = "",
    val description: String = "",
    val timestamp: String =
        "" // LocalDateTime is converted to String to ensure Firestore compatibility
) {
  companion object {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    /**
     * Converts a TripNotification model to a FirestoreTripNotification DTO.
     *
     * @param tripNotification The TripNotification object to convert.
     * @return A Firestore-compatible FirestoreTripNotification DTO.
     */
    fun fromTripNotification(tripNotification: TripNotification): FirestoreTripNotification {
      return FirestoreTripNotification(
          notificationId = tripNotification.notificationId,
          userId = tripNotification.userId,
          title = tripNotification.title,
          userName = tripNotification.userName,
          description = tripNotification.description,
          timestamp =
              tripNotification.timestamp.format(formatter) // Convert LocalDateTime to String
          )
    }
  }

  /**
   * Converts this FirestoreTripNotification DTO back to a TripNotification model.
   *
   * @return A TripNotification object with the original LocalDateTime timestamp restored.
   */
  fun toTripNotification(): TripNotification {
    return TripNotification(
        notificationId = this.notificationId,
        userId = this.userId,
        title = this.title,
        userName = this.userName,
        description = this.description,
        timestamp =
            LocalDateTime.parse(this.timestamp, formatter) // Convert String back to LocalDateTime
        )
  }
}
