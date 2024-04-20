package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val tripsRepository: TripsRepository): ViewModel() {
    //delete a user from the database
     fun deleteUser(userId: String, tripId: String) {
        viewModelScope.launch {
             tripsRepository.removeUserFromTrip(userId, tripId)
         }
    }
}