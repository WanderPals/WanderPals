package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class StopsListViewModel(
    private val tripsRepository: TripsRepository,
  private val tripId: String,
) : ViewModel() {

  private val _stops = MutableStateFlow(emptyList<Stop>())
  open var stops: StateFlow<List<Stop>> = _stops.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  /** Fetches all stops from the trip and updates the state flow accordingly. */
  open fun loadStops() {
    viewModelScope.launch {
      _isLoading.value = true
      _stops.value = tripsRepository.getAllStopsFromTrip(tripId)
      _isLoading.value = false
    }
  }

  /** Factory for creating StopsListViewModel instances. */
  class StopsListViewModelFactory(
      private val tripsRepository: TripsRepository,
    private val tripId: String,
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(StopsListViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return StopsListViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
