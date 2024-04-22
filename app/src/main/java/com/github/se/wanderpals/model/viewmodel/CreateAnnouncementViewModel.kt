package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for creating Announcement. This ViewModel is responsible for adding a Announcement to a
 * trip.
 */
open class CreateAnnouncementViewModel(tripsRepository: TripsRepository) : ViewModel() {
  private val _tripsRepository = tripsRepository

  /**
   * Adds a Announcement by the administrator to a trip.
   *
   * @param tripId The ID of the trip to which the Announcement is to be added.
   * @param announcement The Announcement object to be added.
   * @return A boolean representing the success of the operation.
   */
  open fun addAnnouncement(tripId: String, announcement: Announcement): Boolean {
    var a: Boolean = true
    viewModelScope.launch {
      _tripsRepository.addAnnouncementToTrip(tripId, announcement).also { a = it }
    }
    return a
  }

  class CreateAnnouncementViewModelFactory(private val tripsRepository: TripsRepository) :
      ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(CreateAnnouncementViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return CreateAnnouncementViewModel(tripsRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
