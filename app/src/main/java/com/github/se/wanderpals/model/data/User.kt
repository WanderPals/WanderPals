package com.github.se.wanderpals.model.data

/**
 * Defines user details within the application, including their roles and permissions.
 *
 * @property userId Unique identifier for the user.
 * @property name User's full name for display and identification.
 * @property email User's email address, used for communication and login.
 * @property role User's role within the application, influencing access and capabilities.
 * @property permissions List of specific actions the user is permitted to perform, allowing for
 *   detailed access control.
 */
data class User(
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val permissions: List<String> = emptyList()
)
