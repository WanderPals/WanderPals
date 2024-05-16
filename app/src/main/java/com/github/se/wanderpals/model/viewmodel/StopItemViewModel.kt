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
 *
 * @param tripsRepository the repository for trips
 * @param tripId the id of the trip
 */
open class StopItemViewModel(
    private val tripsRepository: TripsRepository,
    private val tripId: String
) : ViewModel() {

  private val _isDeleting = MutableStateFlow(false)
  open val isDeleting: StateFlow<Boolean> = _isDeleting

  /** Function to reset the state of the delete operation. */
  fun resetDeleteState() {
    _isDeleting.value = false
  }

  /**
   * Function to delete a stop from the trip.
   *
   * @param stopId the id of the stop to be deleted
   */
  open fun deleteStop(stopId: String) {
    viewModelScope.launch {
      tripsRepository.removeStopFromTrip(tripId, stopId)
      _isDeleting.value = true
    }
  }

  /** Factory for creating StopViewModel instances. */
  class StopItemViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String,
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(StopItemViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return StopItemViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
