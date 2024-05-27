package com.github.se.wanderpals.model.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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

  open fun modifyCurrentUserName(name: String) {
    currentUser.value = currentUser.value?.copy(name = name)
    SessionManager.setName(name)
    val user = listOfUsers.value.find { it.userId == currentUser.value?.userId }
    if (user != null) {
      modifyUser(user.copy(name = name))
    }

    viewModelScope.launch {
      // Get all the suggestions, and update the name of the user in the suggestions
      val suggestions = tripsRepository.getAllSuggestionsFromTrip(tripId)
      suggestions.forEach { suggestion ->
        var updatedSuggestion = suggestion

        // Update the name in the suggestion if the userId matches
        if (suggestion.userId == currentUser.value?.userId) {
          updatedSuggestion = updatedSuggestion.copy(userName = name)
        }

        // Update the name in the comments if the userId matches
        val updatedComments =
            updatedSuggestion.comments.map { comment ->
              if (comment.userId == currentUser.value?.userId) {
                comment.copy(userName = name)
              } else {
                comment
              }
            }

        // Only update the suggestion if there are changes
        if (updatedSuggestion != suggestion || updatedComments != suggestion.comments) {
          tripsRepository.updateSuggestionInTrip(
              tripId, updatedSuggestion.copy(comments = updatedComments))
        }
      }

      // Get all the expenses, and update the name of the user in the expenses
      val expenses = tripsRepository.getAllExpensesFromTrip(tripId)
      expenses.forEach { expense ->
        if (expense.userId == currentUser.value?.userId) {
          tripsRepository.updateExpenseInTrip(tripId, expense.copy(userName = name))
        }
      }

      // Get all the announcements, and update the name of the user in the announcements
      val announcements = tripsRepository.getAllAnnouncementsFromTrip(tripId)
      announcements.forEach { announcement ->
        if (announcement.userId == currentUser.value?.userId) {
          tripsRepository.updateAnnouncementInTrip(tripId, announcement.copy(userName = name))
        }
      }
    }
  }

  // Modify the current user's profil photo
  open fun modifyCurrentUserProfilePhoto(profilePhoto: String) {
    if (!SessionManager.getIsNetworkAvailable()) return // Check if the network is available
    currentUser.value = currentUser.value?.copy(profilePhoto = profilePhoto)
    SessionManager.setPhoto(profilePhoto)
    val user = listOfUsers.value.find { it.userId == currentUser.value?.userId }
    if (user != null) {
      modifyUser(user.copy(profilePictureURL = profilePhoto))
      FirebaseAuth.getInstance()
          .currentUser
          ?.updateProfile(
              UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(profilePhoto)).build())
    }
  }
  // Send a a Uri to the repository to upload the image

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
