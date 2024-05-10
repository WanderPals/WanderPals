package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Firestore-compatible DTO for a Suggestion, adapting complex objects (Stop, Comment) and
 * LocalDate, LocalTime to String for compatibility with Firestore's data model. Includes methods
 * for conversion between this Firestore-compatible version and the data model.
 *
 * @property suggestionId Unique identifier of the suggestion.
 * @property userId ID of the user who made the suggestion.
 * @property userName Name of the suggesting user, for display purposes.
 * @property text Content of the suggestion.
 * @property createdAt Creation date of the suggestion in format (yyyy-MM-dd).
 * @property createdAtTime Creation time of the suggestion in format (HH:mm).
 * @property stop Detailed proposed Stop, in a Firestore-compatible format.
 * @property comments List of comments on the suggestion, in Firestore-compatible formats.
 * @property userLikes List of user IDs who liked the suggestion.
 * @property stopStatus Status of the stop addition, converted to String for Firestore.
 */
data class FirestoreSuggestion(
    val suggestionId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: String = "", // Converted to String for Firestore compatibility
    val createdAtTime: String = "", // Converted to String for Firestore compatibility
    val stop: FirestoreStop = FirestoreStop(), // Using Firestore-compatible Stop object
    val comments: List<FirestoreComment> =
        emptyList(), // Using Firestore-compatible Comment objects
    val userLikes: List<String> = emptyList(),
    val stopStatus: String = CalendarUiState.StopStatus.NONE.name // Convert enum to string for Firestore
) {
  companion object {
    /**
     * Converts a Suggestion domain model to FirestoreSuggestion DTO.
     *
     * @param suggestion The Suggestion object to convert, including data models for Stop and
     *   Comments.
     * @return A Firestore-compatible FirestoreSuggestion DTO.
     */
    fun fromSuggestion(suggestion: Suggestion): FirestoreSuggestion {
      val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
      val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
      return FirestoreSuggestion(
          suggestionId = suggestion.suggestionId,
          userId = suggestion.userId,
          userName = suggestion.userName,
          text = suggestion.text,
          createdAt = suggestion.createdAt.format(dateFormatter),
          createdAtTime =
              suggestion.createdAtTime.format(timeFormatter), // Convert LocalTime to String
          stop = FirestoreStop.fromStop(suggestion.stop), // Convert Stop to FirestoreStop
          comments =
              suggestion.comments.map {
                FirestoreComment.fromComment(it)
              }, // Convert each Comment to FirestoreComment
          userLikes = suggestion.userLikes,
          stopStatus = suggestion.stopStatus.name // Convert enum to string for Firestore
      )
    }
  }

  /**
   * Converts this FirestoreSuggestion DTO back into a data model Suggestion.
   *
   * @return A Suggestion object with parsed LocalDate, LocalTime fields, and converted Stop and
   *   Comment objects.
   */
  fun toSuggestion(): Suggestion {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    return Suggestion(
        suggestionId = suggestionId,
        userId = userId,
        userName = userName,
        text = text,
        createdAt =
            LocalDate.parse(createdAt, dateFormatter), // Parse date string back into LocalDate
        createdAtTime =
            LocalTime.parse(createdAtTime, timeFormatter), // Parse time string back into LocalTime
        stop = stop.toStop(), // Convert FirestoreStop back to Stop
        comments = comments.map { it.toComment() }, // Convert each FirestoreComment back to Comment
        userLikes = userLikes,
        stopStatus = CalendarUiState.StopStatus.valueOf(stopStatus) // Convert string back to enum
    )
  }
}
