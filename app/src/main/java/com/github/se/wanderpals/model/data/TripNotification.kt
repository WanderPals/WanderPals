package com.github.se.wanderpals.model.data

import java.time.LocalDateTime

/**
 * Represents a notification related to a user's trip within the application. TripNotifications can
 * be triggered by various events related to the user's travel itinerary.
 *
 * @param title A brief title of the TripNotification.
 * @param route The route of the navigation (can be empty).
 * @param timestamp The exact date and time when the TripNotification was created.
 * @param navActionVariables The navigation action variables stored as a string.
 */
data class TripNotification(
    val title: String,
    val route: String,
    val timestamp: LocalDateTime,
    val navActionVariables: String = ""
)
