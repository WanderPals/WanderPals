package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

open class NotificationsViewModel(private val tripsRepository: TripsRepository) : ViewModel() {
    val notification1 = TripNotification(
        notificationId = "1",
        userId = "user123",
        title = "New Trip Alert",
        userName = "John Doe",
        description = "A new trip has been scheduled for next week.",
        timestamp = LocalDateTime.now()
    )

    val notification2 = TripNotification(
        notificationId = "2",
        userId = "user456",
        title = "Trip Reminder",
        userName = "Jane Smith",
        description = "Don't forget your trip tomorrow!",
        timestamp = LocalDateTime.now().minusDays(1)
    )

    val notification3 = TripNotification(
        notificationId = "3",
        userId = "user789",
        title = "Trip Cancellation",
        userName = "Alice Johnson",
        description = "Unfortunately, your trip for next month has been canceled.",
        timestamp = LocalDateTime.now()
    )
    val tempList = listOf(notification1,notification2,notification3,notification1,notification2,notification3,notification1,notification2,notification3)

    private val _notifStateList = MutableStateFlow(tempList)
    open val notifStateList: StateFlow<List<TripNotification>> = _notifStateList

    private val _announcementStateList = MutableStateFlow(tempList)
    open val announcementStateList: StateFlow<List<TripNotification>> = _announcementStateList
    // State flow to track loading state
    private val _isLoading = MutableStateFlow(true)
    open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


}