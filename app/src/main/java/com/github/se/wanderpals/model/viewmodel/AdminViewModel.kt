package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
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
  // delete a user from the database
  open fun deleteUser(userId: String) {
    viewModelScope.launch { tripsRepository.removeUserFromTrip(userId, tripId) }
  }

  init {
    getUsers()
  }
  // get all the users from the trip
  open fun getUsers() {
    viewModelScope.launch { listOfUsers.value += tripsRepository.getAllUsersFromTrip(tripId) }
  }
  // Push a modified user to the database
  open fun modifyUser(user: User) {
    viewModelScope.launch { tripsRepository.updateUserInTrip(tripId, user) }
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