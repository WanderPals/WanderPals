package com.github.se.wanderpals.model.data

import java.time.LocalDate

/**
 * Represents a log of activities performed by users within the application. Used to track user
 * interactions and changes to various entities like trips, stops, and suggestions.
 *
 * @param logId Unique identifier for the log entry.
 * @param userId Identifier of the user who performed the action.
 * @param userName Name of the user for easier identification in logs.
 * @param action Descriptive string of the action performed (e.g., "added_suggestion").
 * @param entityId Optional. The ID of the entity (trip, stop, suggestion, etc.) affected by the
 *   actions.
 * @param entityType Optional. Type of the entity affected, helps in filtering logs by entity type.
 * @param description Optional. Additional details or context about the action performed.
 * @param createdAt The date when the action was performed.
 */
data class ActivityLog(
    val logId: String,
    val userId: String,
    val userName: String,
    val action: String,
    val entityId: String? = null,
    val entityType: String? = null,
    val description: String? = null,
    val createdAt: LocalDate
)
