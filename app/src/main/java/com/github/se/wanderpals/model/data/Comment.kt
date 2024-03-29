package com.github.se.wanderpals.model.data

import java.time.LocalDate

/**
 * Represents user-generated comments within the application, primarily associated with suggestions.
 * These comments allow users to share feedback and engage in discussions about trip planning.
 *
 * @param commentId The unique identifier of the comment.
 * @param userId The ID of the user who posted the comment, linking it to a specific user profile.
 * @param userName The name of the user, displayed alongside the comment for identification.
 * @param text The content of the comment provided by the user.
 * @param createdAt The date on which the comment was made, used for sorting and display.
 */
data class Comment(
    val commentId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: LocalDate
)
