package com.github.se.wanderpals.model.data

import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents user-generated comments within the application, primarily associated with suggestions
 * for trips. These comments enable users to engage in discussions, provide feedback, and
 * participate actively in trip planning.
 *
 * @param commentId The unique identifier of the comment, typically generated by the database.
 * @param userId The ID of the user who posted the comment, linking it back to their user profile.
 * @param userName The name of the user, displayed next to the comment for easy identification.
 * @param text The actual text content of the comment, expressing the user's thoughts or feedback.
 * @param createdAt The date on which the comment was posted, used to sort comments chronologically.
 * @param createdAtTime The specific time at which the comment was posted, enhancing the precision
 *   of comment timing, particularly useful for displaying recent interactions or during active
 *   discussions.
 */
data class Comment(
    val commentId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: LocalDate,
    val createdAtTime:
        LocalTime // Time of creation adds specificity to sorting and displaying comments.
)
