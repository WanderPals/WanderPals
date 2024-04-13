package com.github.se.wanderpals.model.data

/**
 * Defines user details within the application, including their roles and permissions. This class
 * now includes additional fields such as nickname, lastPosition, and profilePictureURL.
 *
 * @param userId Unique identifier for the user.
 * @param name User's full name for display and identification.
 * @param email User's email address, used for communication and login.
 * @param nickname User's nickname for casual display or alternative identification.
 * @param role User's role within the application, influencing access and capabilities.
 * @param lastPosition Geographic coordinates representing the user's last known position.
 * @param profilePictureURL URL to the user's profile picture.
 */
data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val nickname: String = "",
    val role: Role,
    val lastPosition: GeoCords,
    val profilePictureURL: String = ""
)
