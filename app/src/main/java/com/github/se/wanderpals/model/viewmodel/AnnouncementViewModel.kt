package com.github.se.wanderpals.model.viewmodel

import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager

class AnnouncementViewModel(private val tripsRepository: TripsRepository) {

  /**
   * Attempts to delete a trip Announcement. Only allows deletion if the current user is an admin.
   *
   * @param tripId The ID of the trip from which the Announcement is to be deleted.
   * @param announcement The Announcement object to be deleted.
   * @return A boolean representing the success of the operation.
   */
  suspend fun deleteAnnouncement(tripId: String, announcement: Announcement): Boolean {
    // Check if the current user has permission to remove items, only admins can delete
    // Announcements
    return if (SessionManager.canRemove(announcement.userId)) {
      tripsRepository.removeAnnouncementFromTrip(tripId, announcement.announcementId)
    } else {
      false
    }
  }

  // TO add once the view model extends the viewModel class and become sa proper view model
  /*
  class AnnouncementViewModelFactory(private val tripsRepository: TripsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return AnnouncementViewModel(tripsRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }

   */
}
