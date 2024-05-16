package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import kotlinx.coroutines.launch

class SessionViewModel(private val tripsRepository: TripsRepository) : ViewModel() {


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
  fun updateUserForCurrentUser(tripId: String) {
    val userId = SessionManager.getCurrentUser()?.userId
    if (userId != null) {
      viewModelScope.launch {
        try {
          val user = tripsRepository.getUserFromTrip(tripId, userId)
          if (user == null) {
            Log.d("SessionViewModel", "Failed to find user with userId $userId , in trip $tripId")
          } else {
            SessionManager.setRole(user.role)
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
