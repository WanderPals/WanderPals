package com.github.se.wanderpals.model.data

/**
 * Defines user details within the application, including their roles and permissions. (Currently
 * all field have a default value for serialization for firebase/ in refactor will most likely add a
 * DTO for this object)
 *
 * @param userId Unique identifier for the user.
 * @param name User's full name for display and identification.
 * @param email User's email address, used for communication and login.
 * @param role User's role within the application, influencing access and capabilities.
 * @param permissions List of specific actions the user is permitted to perform, allowing for
 *   detailed access control.
 */
data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val permissions: List<String> = emptyList()
)
