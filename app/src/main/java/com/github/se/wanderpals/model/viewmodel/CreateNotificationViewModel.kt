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

  /**
   * Adds a notification by the administrator to a trip.
   *
   * @param tripId The ID of the trip to which the notification is to be added.
   * @param tripNotification The notification object to be added.
   * @return A boolean representing the success of the operation.
   */
  open fun addNotification(tripId: String, tripNotification: TripNotification): Boolean {
    var a: Boolean = true
    viewModelScope.launch {
      _tripsRepository.addTripNotificationToTrip(tripId, tripNotification).also { a = it }
    }
    return a
  }

  /*
  // This is for @Preview purposes only
  fun addNotification(tripId: String, tripNotification: TripNotification): Boolean {
    return true
  }
    */
}
