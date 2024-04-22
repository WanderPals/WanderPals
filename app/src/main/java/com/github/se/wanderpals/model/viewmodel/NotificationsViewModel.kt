package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class NotificationsViewModel(val tripsRepository: TripsRepository,val tripId : String) : ViewModel() {
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
            notification3
        )

    private val _notifStateList = MutableStateFlow(tempList)
    open val notifStateList: StateFlow<List<TripNotification>> = _notifStateList

    private val _announcementStateList = MutableStateFlow(emptyList<Announcement>())
    open val announcementStateList: StateFlow<List<Announcement>> = _announcementStateList

    // State flow to track loading state
    private val _isLoading = MutableStateFlow(true)
    open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init{
        getAllStateList()
    }
    open fun getAllStateList() {
        viewModelScope.launch {
            // Set loading state to true before fetching data
            _isLoading.value = true
            // Fetch all trips from the repository
            _announcementStateList.value = tripsRepository.getAllAnnouncementsFromTrip(tripId)
            // Set loading state to false after data is fetched
            _isLoading.value = false
        }
    }
    /**
     * Adds a Announcement by the administrator to a trip.
     *
     * @param tripId The ID of the trip to which the Announcement is to be added.
     * @param announcement The Announcement object to be added.
     * @return A boolean representing the success of the operation.
     */
    open fun addAnnouncement(tripId: String, announcement: Announcement): Boolean {
        var success: Boolean = true
        viewModelScope.launch {
            success =
                tripsRepository.addAnnouncementToTrip(tripId, announcement)
        }
        return success
    }
}
