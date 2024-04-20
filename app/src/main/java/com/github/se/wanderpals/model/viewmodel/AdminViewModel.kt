package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.github.se.wanderpals.model.data.User

open class AdminViewModel(private val tripsRepository: TripsRepository, private val tripId: String) : ViewModel() {
  open var listOfUsers = MutableStateFlow(emptyList<User>())
  // delete a user from the database
  fun deleteUser(userId: String) {
    viewModelScope.launch { tripsRepository.removeUserFromTrip(userId, tripId) }
  }

  init {
    getUsers()
  }
  //get all the users from the trip
    private fun getUsers(){
      viewModelScope.launch {
        listOfUsers.value += tripsRepository.getAllUsersFromTrip(tripId)
      }
  }

}
