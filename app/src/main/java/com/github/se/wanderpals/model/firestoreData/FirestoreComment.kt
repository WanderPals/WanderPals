package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.Comment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * A Firestore-compatible representation of a user-generated comment. This class adapts the Comment
 * data model for Firestore by converting LocalDate and LocalTime fields to String. It facilitates
 * storing and retrieving comment data from Firestore, ensuring that all date and time information
 * remains consistent and formatted correctly for database operations.
 *
 * @param commentId Unique identifier of the comment.
 * @param userId ID of the user who posted the comment.
 * @param userName Name of the user, displayed alongside the comment.
 * @param text Content of the comment.
 * @param createdAt Date the comment was made, in ISO-8601 format (yyyy-MM-dd).
 * @param createdAtTime Time the comment was made, in ISO-8601 format (HH:mm:ss).
 */
data class FirestoreComment(
    val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val createdAt: String = "",
    val createdAtTime: String = ""
) {
  companion object {
    /**
     * Converts a domain model Comment to a FirestoreComment DTO, formatting the date and time to
     * strings suitable for Firestore storage.
     *
     * @param comment The Comment object to convert.
     * @return A FirestoreComment DTO with the date and time properly formatted as strings.
     */
    fun fromComment(comment: Comment): FirestoreComment {
      val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
      val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
      return FirestoreComment(
          commentId = comment.commentId,
          userId = comment.userId,
          userName = comment.userName,
          text = comment.text,
          createdAt = comment.createdAt.format(dateFormatter),
          createdAtTime = comment.createdAtTime.format(timeFormatter))
    }
  }

  /**
   * Converts this FirestoreComment DTO back into a domain model Comment, parsing the string
   * representations of date and time back into LocalDate and LocalTime objects.
   *
   * @return A Comment object with parsed LocalDate and LocalTime fields.
   */
  fun toComment(): Comment {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    return Comment(
        commentId = commentId,
        userId = userId,
        userName = userName,
        text = text,
        createdAt = LocalDate.parse(createdAt, dateFormatter),
        createdAtTime = LocalTime.parse(createdAtTime, timeFormatter))
  }
}
