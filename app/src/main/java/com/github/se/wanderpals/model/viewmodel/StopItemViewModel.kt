package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
    * ViewModel for a stop item in a trip.
    * @param stopId the id of the stop
    * @param tripsRepository the repository for trips
    * @param tripId the id of the trip
 */
class StopItemViewModel(
    private val stopId: String,
    private val tripsRepository: TripsRepository,
    private val tripId: String
) : ViewModel() {

  private val _isDeleted = MutableStateFlow(false)
  val isDeleted: StateFlow<Boolean> = _isDeleted

  fun deleteStop() {
    viewModelScope.launch {
      tripsRepository.removeStopFromTrip(tripId, stopId)
      _isDeleted.value = true
    }
  }

  /** Factory for creating StopViewModel instances. */
  class StopItemViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String,
      private val stopId: String
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(StopItemViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return StopItemViewModel(stopId, tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
