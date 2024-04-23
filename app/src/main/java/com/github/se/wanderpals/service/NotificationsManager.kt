package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

object NotificationsManager {
    private lateinit var tripsRepository: TripsRepository

    fun initNotificationsManager(repository: TripsRepository) {
        tripsRepository = repository
    }

    suspend fun addJoinTripNotification(tripId : String){
        var notifList = tripsRepository.getNotificationList(tripId).toMutableList()
        val newNotif = TripNotification(
            "${SessionManager.getCurrentUser()!!.name} joined the trip ",
            "",
            LocalDateTime.now())
        notifList.add(newNotif)
        tripsRepository.setNotificationList(tripId,notifList.toList())

    }

}