package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class MembersViewModel(private val tripsRepository: TripsRepository, tripId: String) :
    ViewModel() {

  private val _members = MutableStateFlow(emptyList<User>())
  open val members: StateFlow<List<User>> = _members.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  init {
    loadMembers(tripId)
  }

  /** Fetches all trips from the repository and updates the state flow accordingly. */
  open fun loadMembers(tripId: String) {
    viewModelScope.launch {
      _isLoading.value = true
      // Fetch all trips from the repository
      _members.value = tripsRepository.getAllUsersFromTrip(tripId)
      _isLoading.value = false
    }
  }
}
