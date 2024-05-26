package com.github.se.wanderpals.model.data

/**
 * Data class representing a broadcast notification.
 *
 * @param targetToken The list of target tokens for the notification.
 * @param body The body of the notification.
 * @param title The title of the notification.
 */
data class BroadcastNotification(
    val targetToken: List<String> = emptyList(),
    val body: String = "",
    val title: String = ""
)
