package com.github.se.wanderpals.service

import android.net.Uri
import android.util.Log
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Role
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

/** Represents a simplified user model within the session management context. */
data class SessionUser(
    val userId: String = "",
    var name: String = "",
    var email: String = "",
    var role: Role = Role.VIEWER,
    var geoCords: GeoCords = GeoCords(0.0, 0.0),
    var profilePhoto: String = default_profile_photo,
    var tripName: String = "",
    val nickname: String = ""
)

const val default_profile_photo =
    "https://firebasestorage.googleapis.com/v0/b/wanderpals.appspot.com/o/images%2FDEFAULT_PROFILE_PHOTO.png?alt=media&token=a57fa34b-87ea-47e1-ae72-21a94178940e"

/**
 * Global object to manage session information such as current user and active trip details.
 * Provides utility methods to handle user permissions based on roles and trip data.
 */
object SessionManager {

  private var currentUser: SessionUser? = null

  private var currentUserNotificationTokenId: String = ""

  /**
   * Sets or updates the current user session with provided details.
   *
   * @param userId The user's unique identifier, optional.
   * @param name The user's full name, optional.
   * @param email The user's email address, optional.
   * @param role The user's role within the application, optional.
   * @param geoCords The user's geographic coordinates, optional.
   */
  @Suppress("UselessCallOnNotNull")
  fun setUserSession(
      userId: String = currentUser?.userId ?: "",
      name: String = currentUser?.name ?: "",
      email: String = currentUser?.email ?: "",
      role: Role = currentUser?.role ?: Role.VIEWER,
      geoCords: GeoCords = currentUser?.geoCords ?: GeoCords(0.0, 0.0),
      profilePhoto: String = currentUser?.profilePhoto ?: default_profile_photo,
      tripName: String = currentUser?.tripName ?: "",
      nickname: String = currentUser?.nickname ?: ""
  ) {
    val profilePhotoUse = profilePhoto.isNullOrEmpty().let { default_profile_photo }
    currentUser =
        SessionUser(userId, name, email, role, geoCords, profilePhotoUse, tripName, nickname)

    if (FirebaseAuth.getInstance().currentUser?.photoUrl.toString().isNullOrEmpty()) {
      FirebaseAuth.getInstance()
          .currentUser
          ?.updateProfile(
              UserProfileChangeRequest.Builder()
                  .setPhotoUri(Uri.parse(profilePhotoUse))
                  .setDisplayName(name)
                  .build())
    }
  }

  /**
   * Retrieves the current user's notification token.
   *
   * @return the notification token ID of the current user.
   */
  fun getNotificationToken(): String {
    return currentUserNotificationTokenId
  }

  /**
   * Updates the current user's notification token.
   *
   * @param currentUserNotificationTokenId the new notification token ID to be set.
   */
  fun setNotificationToken(currentUserNotificationTokenId: String) {
    this.currentUserNotificationTokenId = currentUserNotificationTokenId
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
    return isAdmin() || currentUser?.userId == userId
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

  /** Get the position of the current user */
  fun getPosition(): LatLng {
    return LatLng(currentUser?.geoCords?.latitude ?: 0.0, currentUser?.geoCords?.longitude ?: 0.0)
  }

  /** Was the position set */
  fun isPositionSet(): Boolean {
    return currentUser?.geoCords?.latitude != 0.0 && currentUser?.geoCords?.longitude != 0.0
  }

  /**
   * Updates the role of the current user.
   *
   * @param role New role to assign to the user.
   */
  fun setRole(role: Role) {
    currentUser?.role = role
  }

  fun setPhoto(photoUrl: String) {
    currentUser?.profilePhoto = photoUrl
  }

  fun setTripName(tripName: String) {
    currentUser?.tripName = tripName
  }

  /** Clears the current user session, effectively logging out the user. */
  fun logout() {
    currentUser = null
  }
}
