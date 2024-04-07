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
open class OverviewViewModel(private val tripsRepository: TripsRepository) : ViewModel() {

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
  /** Adds the trip for the user by adding it in it trips Repository
   *
   * @param trip The trip to add in the repository.
   *
   */
  open fun createTrip(trip: Trip) {
    runBlocking { tripsRepository.addTrip(trip) }
  }

  /**
   * Adds the user to a trip with the specified trip ID. The trip must already exist in the database
   * to make this function success.
   *
   * @param tripId The ID of the trip to join.
   * @return True if the user successfully joins the trip, false otherwise.
   */
  open fun joinTrip(tripId: String): Boolean {
    var success = false
    runBlocking {
      success = tripsRepository.addTripId(tripId)
      if (success) {
        // update the state of the user by adding the new trip in its list of trips
        val newState = _state.value.toMutableList()
        val newTrip = tripsRepository.getTrip(tripId)!!
        newState.add(newTrip)
        _state.value = newState.toList()
      }
    }
    return success
  }

}
