package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.launch

/**
 * View model for the Map screen, containing the data and logic for the screen.
 *
 * @param tripsRepository The repository for trips data.
 */
class MapViewModel(tripsRepository: TripsRepository) : ViewModel() {
  private val _tripsRepository = tripsRepository
  private var _stops = mutableListOf<Stop>()

  // add a stop to the trip
  fun addStop(tripId: String, stop: Stop) {
    viewModelScope.launch { _tripsRepository.addStopToTrip(tripId, stop) }
  }
  // get all stops from the trip
  fun getAllStops(tripId: String): List<Stop> {
    viewModelScope.launch { _stops = _tripsRepository.getAllStopsFromTrip(tripId).toMutableList() }
    return _stops
  }
}
