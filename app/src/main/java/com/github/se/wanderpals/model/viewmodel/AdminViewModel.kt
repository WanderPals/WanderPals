package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * AdminViewModel is a ViewModel class that is used to manage the Admin screen. This class is used
 * to manage the Admin screen and handle the business logic of the Admin screen.
 *
 * @param tripsRepository The repository that contains the trips.
 * @param tripId The id of the trip.
 */
open class AdminViewModel(
    private val tripsRepository: TripsRepository,
    private val tripId: String
) : ViewModel() {
  open var listOfUsers = MutableStateFlow(emptyList<User>())
  open var currentUser = MutableStateFlow(SessionManager.getCurrentUser())

  // delete a user from the database
  open fun deleteUser(userId: String) {
    viewModelScope.launch { tripsRepository.removeUserFromTrip(tripId, userId) }
    listOfUsers.value = listOfUsers.value.filter { it.userId != userId }
  }

  // get all the users from the trip
  open fun getUsers() {
    viewModelScope.launch { listOfUsers.value = tripsRepository.getAllUsersFromTrip(tripId) }
    currentUser.value = SessionManager.getCurrentUser()
  }
  // Push a modified user to the database
  open fun modifyUser(user: User) {
    viewModelScope.launch { tripsRepository.updateUserInTrip(tripId, user) }
    listOfUsers.value = listOfUsers.value.map { if (it.userId == user.userId) user else it }
    if (user.userId == currentUser.value?.userId) {
      SessionManager.setRole(user.role)
      SessionManager.setPhoto(user.profilePictureURL)
      currentUser.value = SessionManager.getCurrentUser()
    }
  }

  // Modify the current user's role
  open fun modifyCurrentUserRole(role: Role) {
    currentUser.value = currentUser.value?.copy(role = role)
    SessionManager.setRole(role)
    val user = listOfUsers.value.find { it.userId == currentUser.value?.userId }
    if (user != null) {
      modifyUser(user.copy(role = role))
    }
  }

  // Modify the current user's profil photo
  open fun modifyCurrentUserProfilePhoto(profilePhoto: String) {
    currentUser.value = currentUser.value?.copy(profilePhoto = profilePhoto)
    SessionManager.setPhoto(profilePhoto)
    val user = listOfUsers.value.find { it.userId == currentUser.value?.userId }
    if (user != null) {
      modifyUser(user.copy(profilePictureURL = profilePhoto))
    }
  }

  class AdminViewModelFactory(
      private val tripId: String,
      private val tripsRepository: TripsRepository
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return AdminViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
