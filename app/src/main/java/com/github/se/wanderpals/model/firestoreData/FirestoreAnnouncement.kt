package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Announcement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Firestore-compatible DTO for a Announcement, simplifying LocalDateTime to String for
 * compatibility and ensuring that all announcement details are correctly handled for interactions
 * with Firestore.
 *
 * @param announcementId The unique identifier for the announcement.
 * @param userId The identifier of the user or system that posted the announcement.
 * @param title A brief title of the announcement.
 * @param userName The name of the user or system entity that posted the announcement.
 * @param description Detailed description of the announcement.
 * @param timestamp The exact date and time when the announcement was created, stored as a String.
 */
data class FirestoreAnnouncement(
    val announcementId: String = "",
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
     * Converts a Announcement model to a FirestoreAnnouncement DTO.
     *
     * @param tripNotification The Announcement object to convert.
     * @return A Firestore-compatible FirestoreAnnouncement DTO.
     */
    fun fromAnnouncement(tripNotification: Announcement): FirestoreAnnouncement {
      return FirestoreAnnouncement(
          announcementId = tripNotification.announcementId,
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
   * Converts this FirestoreAnnouncement DTO back to a Announcement model.
   *
   * @return A Announcement object with the original LocalDateTime timestamp restored.
   */
  fun toAnnouncement(): Announcement {
    return Announcement(
        announcementId = this.announcementId,
        userId = this.userId,
        title = this.title,
        userName = this.userName,
        description = this.description,
        timestamp =
            LocalDateTime.parse(this.timestamp, formatter) // Convert String back to LocalDateTime
        )
  }
}
