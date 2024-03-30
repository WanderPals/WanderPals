package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.launch

open class CreateTripViewModel(tripsRepository: TripsRepository) : ViewModel() {

  private val _tripsRepository = tripsRepository

  open fun createTrip(trip: Trip) {
    viewModelScope.launch { _tripsRepository.addTrip(trip) }
  }
}
