package com.github.se.wanderpals.model.data

import java.time.LocalDate

data class ActivityLog(
    val logId: String,
    val userId: String,
    val userName: String,
    val action: String, // Describes the action performed, e.g., "added_suggestion"
    val entityId: String? = null, // Optional reference to the entity affected by the action
    val entityType: String? = null, // E.g., "Trip", "Stop", "Suggestion"
    val description: String? = null, // Optional additional details about the action
    val createdAt: LocalDate // Consider using a Date type
)
