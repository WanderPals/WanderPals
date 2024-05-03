package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.TripNotification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Firestore-compatible DTO for TripNotification, simplifying LocalDateTime to String for
 * compatibility and ensuring that all trip notification details are correctly handled for
 * interactions with Firestore.
 *
 * @param title A brief title of the TripNotification.
 * @param route The route of the navigation (can be empty).
 * @param timestamp The exact date and time when the TripNotification was created, stored as a
 *   String.
 * @param navActionVariables The navigation action variables stored as a string.
 */
data class FirestoreTripNotification(
    val title: String = "",
    val route: String = "",
    val timestamp: String =
        "", // LocalDateTime is converted to String to ensure Firestore compatibility
    val navActionVariables: String = ""
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
          title = tripNotification.title,
          route = tripNotification.route,
          timestamp =
              tripNotification.timestamp.format(formatter), // Convert LocalDateTime to String
          navActionVariables = tripNotification.navActionVariables)
    }
  }

  /**
   * Converts this FirestoreTripNotification DTO back to a TripNotification model.
   *
   * @return A TripNotification object with the original LocalDateTime timestamp restored.
   */
  fun toTripNotification(): TripNotification {
    return TripNotification(
        title = this.title,
        route = this.route,
        timestamp =
            LocalDateTime.parse(this.timestamp, formatter), // Convert String back to LocalDateTime
        navActionVariables = this.navActionVariables)
  }
}
