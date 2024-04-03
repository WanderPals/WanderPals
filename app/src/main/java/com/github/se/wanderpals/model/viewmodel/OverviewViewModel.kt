package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel class responsible for managing data and business logic related to the Overview screen.
 * Provides functionality to fetch all trips and exposes the trips data to the UI.
 *
 * @param tripsRepository The repository for accessing trip data.
 */
open class OverviewViewModel(val tripsRepository: TripsRepository) : ViewModel() {

  // State flow to hold the list of trips
  private val _state = MutableStateFlow(emptyList<Trip>())
  open val state: StateFlow<List<Trip>> = _state

  // State flow to track loading state
  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
  init {
    // Fetch all trips when the ViewModel is initialized
    getAllTrips()
  }

  /** Fetches all trips from the repository and updates the state flow accordingly. */
  open fun getAllTrips() {
    viewModelScope.launch {
      // Set loading state to true before fetching data
      _isLoading.value = true
      // Fetch all trips from the repository
      _state.value = tripsRepository.getAllTrips()
      // Set loading state to false after data is fetched
      _isLoading.value = false
    }
  }

  open fun addTrip(tripId : String) : Boolean{
    var success = false
    runBlocking {
      success = tripsRepository.addTripId(tripId)
      if(success){
        val newState = _state.value.toMutableList()
        val newTrip = tripsRepository.getTrip(tripId)!!
        newState.add(newTrip)
        _state.value = newState.toList()

      }
    }
    return success
  }

}
