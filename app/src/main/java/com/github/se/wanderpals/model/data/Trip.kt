package com.github.se.wanderpals.model.data

import java.time.LocalDate

/**
 * Represents the overall structure of a trip within the application. A trip is a planned itinerary
 * that encompasses a series of stops, participants, and suggested activities or locations by users.
 *
 * @property tripId The unique identifier for the trip, generally generated by the database.
 * @property title The name or title of the trip, providing a brief overview or theme.
 * @property startDate The date on which the trip is scheduled to start.
 * @property endDate The date on which the trip is scheduled to end. It should be the same as or after the startDate.
 * @property totalBudget The estimated or allocated budget for the entire trip. This value can help in planning and managing expenses related to the stops and activities.
 * @property description A more detailed description of the trip, which may include information about the purpose, destinations, and any specific instructions or information for the participants.
 * @property stops A list of document IDs referencing the stops sub-collection. These are the specific destinations or points of interest planned for the trip.
 * @property users A list of user IDs indicating the participants of the trip. These IDs link to the users collection to provide details about the trip's attendees.
 * @property suggestions A list of document IDs referencing the suggestions sub-collection. These suggestions are potential stops or activities proposed by users for consideration to be included in the trip itinerary.
 */
data class Trip(
    val tripId: String,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalBudget: Double,
    val description: String,
    // These are IDs of the documents in their respective sub-collections
    val stops: List<String> = emptyList(),
    val users: List<String> = emptyList(),
    val suggestions: List<String> = emptyList()
)
