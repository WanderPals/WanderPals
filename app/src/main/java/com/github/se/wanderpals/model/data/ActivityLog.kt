package com.github.se.wanderpals.model.data

import java.time.LocalDate

/**
 * Represents a log of activities performed by users within the application. Used to track user
 * interactions and changes to various entities like trips, stops, and suggestions.
 *
 * @property logId Unique identifier for the log entry.
 * @property userId Identifier of the user who performed the action.
 * @property userName Name of the user for easier identification in logs.
 * @property action Descriptive string of the action performed (e.g., "added_suggestion").
 * @property entityId Optional. The ID of the entity (trip, stop, suggestion, etc.) affected by the
 *   action.
 * @property entityType Optional. Type of the entity affected, helps in filtering logs by entity
 *   type.
 * @property description Optional. Additional details or context about the action performed.
 * @property createdAt The date when the action was performed.
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
