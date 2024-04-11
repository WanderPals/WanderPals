package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * View model for the Map screen, containing the data and logic for the screen.
 *
 * @param tripsRepository The repository for trips data.
 * @param tripId The trip ID.
 */
open class MapViewModel(tripsRepository: TripsRepository, private val tripId: String) :
    ViewModel() {
  private val _tripsRepository = tripsRepository
  open var stops = MutableStateFlow(emptyList<Stop>())

  /**
   * Add a stop to the trip.
   *
   * @param tripId The trip ID.
   * @param stop The stop to add.
   */
  open fun addStop(tripId: String, stop: Stop) {
    viewModelScope.launch { _tripsRepository.addStopToTrip(tripId, stop) }
  }

  init {
    getAllStops()
  }

  /** Get all stops from the trip. */
  open fun getAllStops() {
    viewModelScope.launch {
      stops.value = _tripsRepository.getAllStopsFromTrip(tripId).toMutableList()
    }
  }
}
