package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import kotlinx.coroutines.launch

/**
 * ViewModel to manage data for the session of the user. It handles the retrieval of the token list
 * for the trip and the update of the user role in the trip.
 *
 * @param tripsRepository The repository for trips data.
 */
class SessionViewModel(private val tripsRepository: TripsRepository) : ViewModel() {

  /**
   * Gets the list of token IDs for the trip and stores them in the SessionManager.
   *
   * @param tripId The unique identifier for the trip.
   */
  fun getTheTokenList(tripId: String) {
    viewModelScope.launch {
      try {
        val tokenList = tripsRepository.getTrip(tripId)?.tokenIds
        if (tokenList != null) {
          SessionManager.setListOfTokensTrip(tokenList)
        }
      } catch (e: Exception) {
        Log.e("SessionViewModel", "Failed to get token list", e)
      }
    }
  }

  /**
   * Updates the user role in the trip for the current user, will also navigate to the overview if
   * the user is not found in the trip.
   *
   * @param tripId The unique identifier for the trip.
   * @param navigationActions The navigation actions to be performed after the user role is updated.
   */
  fun updateUserForCurrentUser(tripId: String, navigationActions: NavigationActions) {
    val userId = SessionManager.getCurrentUser()?.userId
    if (userId != null) {
      viewModelScope.launch {
        try {
          val user = tripsRepository.getUserFromTrip(tripId, userId)
          if (user == null) {
            Log.d("SessionViewModel", "Failed to find user with userId $userId , in trip $tripId")
            navigationActions.navigateTo(Route.OVERVIEW)
          } else {
            SessionManager.setRole(user.role)
            SessionManager.setName(user.name)
            tripsRepository.updateUserInTrip(
                tripId,
                user.copy(profilePictureURL = SessionManager.getCurrentUser()?.profilePhoto!!))
          }
        } catch (e: Exception) {
          Log.e("SessionViewModel", "Failed to update user role", e)
        }
      }
    }
  }

  /**
   * Factory for creating instances of the SessionViewModel. Ensures the ViewModel is constructed
   * with the necessary application context.
   */
  class SessionViewModelFactory(private val tripsRepository: TripsRepository) :
      ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return SessionViewModel(tripsRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
