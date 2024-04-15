package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role

/** Represents a simplified user model within the session management context. */
data class SessionUser(
    val userId: String = "",
    var name: String = "",
    var email: String = "",
    var role: Role = Role.VIEWER,
    var geoCords: GeoCords = GeoCords(0.0, 0.0)
)

/**
 * Global object to manage session information such as current user and active trip details.
 * Provides utility methods to handle user permissions based on roles and trip data.
 */
object SessionManager {

  private var currentUser: SessionUser? = null

  /**
   * Sets or updates the current user session with provided details.
   *
   * @param userId The user's unique identifier, optional.
   * @param name The user's full name, optional.
   * @param email The user's email address, optional.
   * @param role The user's role within the application, optional.
   * @param geoCoords The user's geographic coordinates, optional.
   */
  fun setUserSession(
      userId: String = currentUser?.userId ?: "",
      name: String = currentUser?.name ?: "",
      email: String = currentUser?.email ?: "",
      role: Role = currentUser?.role ?: Role.VIEWER,
      geoCoords: GeoCords = currentUser?.geoCords ?: GeoCords(0.0, 0.0)
  ) {
    currentUser = SessionUser(userId, name, email, role, geoCoords)
  }

  /**
   * Retrieves the current user of the session.
   *
   * @return The current user session details.
   */
  fun getCurrentUser(): SessionUser? = currentUser

  /**
   * Checks if the current user has permissions to remove an item based on roles or ownership.
   *
   * @param userId Optional user ID for additional ownership checks, defaults to empty.
   * @return true if the user has administrative rights or owns the item.
   */
  fun canRemove(userId: String = ""): Boolean {
    return currentUser?.role == Role.ADMIN ||
        currentUser?.role == Role.OWNER ||
        currentUser?.userId == userId
  }

  /**
   * Checks if the current user is an admin or owner.
   *
   * @return true if the user holds administrative or ownership rights.
   */
  fun isAdmin(): Boolean {
    return currentUser?.role == Role.ADMIN || currentUser?.role == Role.OWNER
  }

  /**
   * Updates the geographic coordinates of the current user.
   *
   * @param geoCords New geographic coordinates to set for the user.
   */
  fun setGeoCords(geoCords: GeoCords) {
    currentUser?.geoCords = geoCords
  }

  /**
   * Updates the role of the current user.
   *
   * @param role New role to assign to the user.
   */
  fun setRole(role: Role) {
    currentUser?.role = role
  }

  /** Clears the current user session, effectively logging out the user. */
  fun logout() {
    currentUser = null
  }
}
