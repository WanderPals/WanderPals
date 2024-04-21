package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class NotificationsViewModel(private val tripsRepository: TripsRepository) : ViewModel() {
  val notification1 =
      TripNotification(title = "New Trip Alert", path = "", timestamp = LocalDateTime.now())

  val notification2 =
      TripNotification(
          title = "Trip Reminder",
          path = "",
          timestamp = LocalDateTime.now().minusDays(1),
      )

  val notification3 =
      TripNotification(title = "Trip Cancellation", path = "", timestamp = LocalDateTime.now())
  val announcement =
      Announcement(
          announcementId = "12345", // Replace with actual announcement ID
          userId = "67890", // Replace with actual user ID
          title = "New Announcement",
          userName = "John Doe", // Replace with actual user name
          description = "This is a new announcement!",
          timestamp = LocalDateTime.now() // Replace with actual timestamp
          )

  val tempList =
      listOf(
          notification1,
          notification2,
          notification3,
          notification1,
          notification2,
          notification3,
          notification1,
          notification2,
          notification3)
  val tempList2 = listOf(announcement)

  private val _notifStateList = MutableStateFlow(tempList)
  open val notifStateList: StateFlow<List<TripNotification>> = _notifStateList

  private val _announcementStateList = MutableStateFlow(tempList2)
  open val announcementStateList: StateFlow<List<Announcement>> = _announcementStateList
  // State flow to track loading state
  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
}
