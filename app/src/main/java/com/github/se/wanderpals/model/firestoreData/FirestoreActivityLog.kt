package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.ActivityLog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Represents a log of activities performed by users within the application. Used to track user
 * interactions and changes to various entities like trips, stops, and suggestions.
 *
 * @param logId Unique identifier for the log entry.
 * @param userId Identifier of the user who performed the action.
 * @param userName Name of the user for easier identification in logs.
 * @param action Descriptive string of the action performed (e.g., "added_suggestion").
 * @param entityId Optional. The ID of the entity (trip, stop, suggestion, etc.) affected by the
 *   action.
 * @param entityType Optional. Type of the entity affected, helps in filtering logs by entity type.
 * @param description Optional. Additional details or context about the action performed.
 * @param createdAt The date when the action was performed.
 */
data class FirestoreActivityLog(
    val logId: String = "",
    val userId: String = "",
    val userName: String = "",
    val action: String = "",
    val entityId: String? = null,
    val entityType: String? = null,
    val description: String? = null,
    val createdAt: String = ""
){
    companion object{
        /**
         * Converts a ActivityLog data model to a FirestoreActivityLog DTO.
         *
         * @param log The ActivityLog object to convert.
         * @return A FirestoreActivityLog DTO with dates converted to String format.
         */
        fun fromActivityLog(log:ActivityLog):FirestoreActivityLog {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            return FirestoreActivityLog(
                logId = log.logId,
                userId = log.userId,
                userName = log.userName,
                action = log.action,
                entityId = log.entityId,
                entityType = log.entityType,
                description = log.description,
                createdAt = log.createdAt.format(formatter)
            )
        }
    }
    /**
     * Converts this FirestoreActivityLog DTO back into a ActivityLog data model.
     *
     * @return A ActivityLog object with LocalDate fields parsed from String.
     */
    fun toActivityLog(): ActivityLog {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return ActivityLog(
            logId = logId,
            userId = userId,
            userName = userName,
            action = action,
            entityId = entityId,
            entityType = entityType,
            description = description,
            createdAt = LocalDate.parse(createdAt, formatter)
        )
    }
}
