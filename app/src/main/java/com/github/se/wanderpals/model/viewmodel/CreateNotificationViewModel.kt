package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for creating notifications. This ViewModel is responsible for adding a notification to
 * a trip.
 */
open class CreateNotificationViewModel(tripsRepository: TripsRepository) : ViewModel() {
  private val _tripsRepository = tripsRepository

  open fun addNotification(tripId: String, tripNotification: TripNotification): Boolean {
    var a: Boolean = true
    viewModelScope.launch {
      _tripsRepository.addTripNotificationToTrip(tripId, tripNotification).also { a = it }
    }
    return a
  }

  /*
  //This is for @Preview purposes only
  fun addNotification(tripId: String, tripNotification: TripNotification) : Boolean{
      return true
  }
  */
}
