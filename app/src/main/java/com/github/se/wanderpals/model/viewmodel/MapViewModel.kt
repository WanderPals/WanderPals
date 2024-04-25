package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import com.google.android.gms.maps.model.LatLng
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
  open var suggestions = MutableStateFlow(emptyList<Stop>())
  open var usersPositions = MutableStateFlow(emptyList<LatLng>())
  open var userNames = MutableStateFlow(emptyList<String>())
  open var userPosition = MutableStateFlow(LatLng(0.0, 0.0))
  open var seeUserPosition = MutableStateFlow(false)

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
    refreshData()
  }

  /** Refresh the data. */
  fun refreshData() {
    getAllStops()
    getAllSuggestions()
    getAllUsersPositions()
  }

  /** Get all stops from the trip. */
  open fun getAllStops() {
    viewModelScope.launch {
      val allStops = _tripsRepository.getAllStopsFromTrip(tripId)
      stops.value = allStops.filter { it.geoCords != GeoCords(0.0, 0.0) }
    }
  }

  /** Get all suggestions from the trip. */
  open fun getAllSuggestions() {
    viewModelScope.launch {
      val allSuggestions =
          _tripsRepository
              .getAllSuggestionsFromTrip(tripId)
              .map { it.stop }
              .filter { it.geoCords != GeoCords(0.0, 0.0) }
      suggestions.value = allSuggestions
    }
  }

  /** Get all users positions from the trip. */
  open fun getAllUsersPositions() {
    viewModelScope.launch {
      val allUsers = _tripsRepository.getAllUsersFromTrip(tripId)
      val allUsersPositions = mutableListOf<LatLng>()
      val allUserNames = mutableListOf<String>()

      for (user in allUsers) {
        if (user.lastPosition != GeoCords(0.0, 0.0) &&
            SessionManager.getCurrentUser()?.userId != user.userId) {
          allUsersPositions.add(LatLng(user.lastPosition.latitude, user.lastPosition.longitude))
          allUserNames.add(user.name)
        }
        if (user.userId == SessionManager.getCurrentUser()?.userId) {
          SessionManager.setGeoCords(user.lastPosition)
          userPosition.value = SessionManager.getPosition()
          if (SessionManager.isPositionSet()) {
            seeUserPosition.value = true
          }
        }
      }
      usersPositions.value = allUsersPositions
      userNames.value = allUserNames
    }
  }

  /**
   * Update the last position of the user.
   *
   * @param latLng The last position of the user.
   */
  open fun updateLastPosition(latLng: LatLng) {
    viewModelScope.launch {
      val userId = SessionManager.getCurrentUser()?.userId ?: ""
      if (userId.isNotEmpty()) {
        val user = _tripsRepository.getUserFromTrip(tripId, userId)
        if (user != null) {
          val geoCords = GeoCords(latLng.latitude, latLng.longitude)
          _tripsRepository.updateUserInTrip(tripId, user = user.copy(lastPosition = geoCords))
          SessionManager.setGeoCords(geoCords)
          userPosition.value = latLng
          if (SessionManager.isPositionSet()) {
            seeUserPosition.value = true
          }
        }
      }
    }
  }

  class MapViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return MapViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
