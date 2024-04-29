package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel (private val tripsRepository: TripsRepository, tripId: String) : ViewModel() {

    private val _users = MutableStateFlow(emptyList<User>())
    open val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        //loadMembers(tripId)
        _users.value = listOf(User("1", "John Doe"), User("2", "Jane Doe"), User("3", "A1"), User("4", "A2"))
    }

    /**
     * Fetches all members from the trip and updates the state flow accordingly.
     */
    open fun loadMembers(tripId: String) {
        viewModelScope.launch {
            _users.value = tripsRepository.getAllUsersFromTrip(tripId)
        }
    }
}
