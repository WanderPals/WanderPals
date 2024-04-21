package com.github.se.wanderpals.model.data

import java.time.LocalDateTime

/**
 * Represents a Announcement related to a user's trip within the application. Announcements can be
 * triggered by users.
 *
 * @param announcementId The unique identifier for the Announcement, usually generated by the
 *   database.
 * @param userId The identifier of the user or system that posted the Announcement. If it's a system
 *   Announcement, this could represent a system user or null if not applicable.
 * @param title A brief title of the Announcement.
 * @param userName The name of the user or system entity that posted the Announcement.
 * @param description Detailed description of the Announcement, elaborating on the reason or details
 *   of the Announcement.
 * @param timestamp The exact date and time when the Announcement was created.
 */
data class Announcement(
    val announcementId: String,
    val userId: String,
    val title: String,
    val userName: String,
    val description: String,
    val timestamp: LocalDateTime
)