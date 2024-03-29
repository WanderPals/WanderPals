package com.github.se.wanderpals.model.data

import java.time.LocalDate

/**
 * Represents a suggestion made by a user for a potential stop or activity within a trip.
 * Suggestions can be voted on or discussed by other users through comments, and if approved, can be
 * added to the trip itinerary as a Stop.
 *
 * @param suggestionId The unique identifier for the suggestion, usually generated by the database.
 * @param userId The ID of the user who made the suggestion, linking it back to the user's profile.
 * @param userName The name of the user who made the suggestion, used for display purposes next to
 *   the suggestion.
 * @param text The main content of the suggestion, detailing what the user is proposing and why.
 * @param createdAt The date when the suggestion was created, which helps in sorting suggestions
 *   chronologically.
 * @param stop A detailed Stop object proposed by the suggestion, which includes location, budget,
 *   and other relevant information. This is directly embedded to simplify the process of converting
 *   an approved suggestion into an actual trip stop.
 * @param comments A list of comments made by other users in response to the suggestion. This
 *   facilitates discussion and feedback on the suggestion, allowing for a collaborative planning
 *   process.
 */
data class Suggestion(
    val suggestionId: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: LocalDate,
    val stop: Stop, // Embed the Stop object directly
    val comments: List<Comment> = emptyList()
)
