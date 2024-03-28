package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Suggestion
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Firestore-compatible DTO for a Suggestion, adapting complex objects (Stop, Comment) and LocalDate
 * to String for compatibility with Firestore's data model. Includes methods for conversion between
 * this Firestore-compatible version and the data model.
 *
 * @property suggestionId Unique identifier of the suggestion.
 * @property userId ID of the user who made the suggestion.
 * @property userName Name of the suggesting user, for display purposes.
 * @property text Content of the suggestion.
 * @property createdAt Creation date of the suggestion in format (yyyy-MM-dd).
 * @property stop Detailed proposed Stop, in a Firestore-compatible format.
 * @property comments List of comments on the suggestion, in Firestore-compatible formats.
 */
data class FirestoreSuggestion(
    val suggestionId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: String = "", // Converted to String for Firestore compatibility
    val stop: FirestoreStop, // Using Firestore-compatible Stop object
    val comments: List<FirestoreComment> = emptyList() // Using Firestore-compatible Comment objects
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
      val formatter = DateTimeFormatter.ISO_LOCAL_DATE
      return FirestoreSuggestion(
          suggestionId = suggestion.suggestionId,
          userId = suggestion.userId,
          userName = suggestion.userName,
          text = suggestion.text,
          createdAt = suggestion.createdAt.format(formatter),
          stop = FirestoreStop.fromStop(suggestion.stop), // Convert Stop to FirestoreStop
          comments =
              suggestion.comments.map {
                FirestoreComment.fromComment(it)
              } // Convert each Comment to FirestoreComment
          )
    }
  }

  /**
   * Converts this FirestoreSuggestion DTO back into a data model Suggestion.
   *
   * @return A Suggestion object with parsed LocalDate fields and converted Stop and Comment
   *   objects.
   */
  fun toSuggestion(): Suggestion {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return Suggestion(
        suggestionId = suggestionId,
        userId = userId,
        userName = userName,
        text = text,
        createdAt = LocalDate.parse(createdAt, formatter), // Parse date string back into LocalDate
        stop = stop.toStop(), // Convert FirestoreStop back to Stop
        comments = comments.map { it.toComment() } // Convert each FirestoreComment back to Comment
        )
  }
}
