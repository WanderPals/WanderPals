package com.github.se.wanderpals.model.firestoreData

import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.GeoCords

/**
 * Firestore-compatible DTO for a User, converting Role to String and GeoCords to a compatible format,
 * ensuring that all user details are correctly handled for interactions with Firestore.
 *
 * @param userId Unique identifier for the user.
 * @param name User's full name for display and identification.
 * @param email User's email address, used for communication and login.
 * @param nickname User's nickname for casual display or alternative identification.
 * @param role String representation of user's role within the application.
 * @param lastPosition GeoCords representation of geographic coordinates.
 * @param profilePictureURL URL to the user's profile picture.
 */
data class FirestoreUser(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val nickname: String = "",
    val role: String = "",
    val lastPosition: GeoCords = GeoCords(0.0,0.0),  // or consider a different representation if more suitable
    val profilePictureURL: String = ""
) {
    companion object {
        /**
         * Converts a User model to a FirestoreUser DTO.
         *
         * @param user The User object to convert.
         * @return A Firestore-compatible FirestoreUser DTO.
         */
        fun fromUser(user: User): FirestoreUser {
            return FirestoreUser(
                userId = user.userId,
                name = user.name,
                email = user.email,
                nickname = user.nickname,
                role = user.role.name,  // Convert enum to String
                lastPosition = user.lastPosition,  // Convert GeoCords to String
                profilePictureURL = user.profilePictureURL
            )
        }
    }

    /**
     * Converts this FirestoreUser DTO back to a User model.
     *
     * @return A User object with role and geographic coordinates restored.
     */
    fun toUser(): User {
        return User(
            userId = this.userId,
            name = this.name,
            email = this.email,
            nickname = this.nickname,
            role = Role.valueOf(this.role),  // Convert String back to enum
            lastPosition = this.lastPosition,  // Convert String back to GeoCords
            profilePictureURL = this.profilePictureURL
        )
    }
}
