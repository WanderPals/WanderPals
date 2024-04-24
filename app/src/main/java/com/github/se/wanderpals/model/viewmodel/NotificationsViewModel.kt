package com.github.se.wanderpals.model.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class NotificationsViewModel(val tripsRepository: TripsRepository, val tripId: String) :
    ViewModel() {

  private val _notifStateList = MutableStateFlow(emptyList<TripNotification>())
  open val notifStateList: StateFlow<List<TripNotification>> = _notifStateList

  private val _announcementStateList = MutableStateFlow(emptyList<Announcement>())
  open val announcementStateList: StateFlow<List<Announcement>> = _announcementStateList

  private val _isLoading = MutableStateFlow(true)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _isNotifSelected = MutableStateFlow(true)
  val isNotifSelected: StateFlow<Boolean> = _isNotifSelected.asStateFlow()

  private val _announcementItemPressed = MutableStateFlow(false)
  val announcementItemPressed: StateFlow<Boolean> = _announcementItemPressed.asStateFlow()

  private val _selectedAnnouncementId = MutableStateFlow("")
  val selectedAnnouncementID: StateFlow<String> = _selectedAnnouncementId.asStateFlow()

  open fun updateStateLists() {
    viewModelScope.launch {
      // Set loading state to true before fetching data
      _isLoading.value = true
      // Fetch all trips from the repository
      _announcementStateList.value = tripsRepository.getAllAnnouncementsFromTrip(tripId)
      _notifStateList.value = tripsRepository.getNotificationList(tripId)
      // Set loading state to false after data is fetched
      _isLoading.value = false
    }
  }
  /**
   * Adds a Announcement by the administrator to a trip.
   *
   * @param announcement The Announcement object to be added.
   * @return A boolean representing the success of the operation.
   */
  open fun addAnnouncement(announcement: Announcement) {
    runBlocking { tripsRepository.addAnnouncementToTrip(tripId, announcement) }
  }

  open fun removeAnnouncement(announcementId: String) {
    runBlocking {
      tripsRepository.removeAnnouncementFromTrip(tripId, announcementId)
      updateStateLists()
    }
  }

  fun setNotificationSelectionState(value: Boolean) {
    _isNotifSelected.value = value
  }

  fun setAnnouncementItemPressState(value: Boolean) {
    _announcementItemPressed.value = value
  }

  fun setSelectedAnnouncementId(announcementId: String) {
    _selectedAnnouncementId.value = announcementId
  }

  class NotificationsViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return NotificationsViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
