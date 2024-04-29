package com.github.se.wanderpals.model.data

import java.time.LocalDateTime

/**
 * Represents a notification related to a user's trip within the application. TripNotifications can
 * be triggered by various events related to the user's travel itinerary.
 *
 * @param title A brief title of the TripNotification.
 * @param path A string path that could represent the itinerary path or related resource.
 * @param timestamp The exact date and time when the TripNotification was created.
 */
data class TripNotification(val title: String, val path: String, val timestamp: LocalDateTime, val navActionVariables:String = "")
