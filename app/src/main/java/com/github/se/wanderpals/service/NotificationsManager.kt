package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.LocalDateTime

object NotificationsManager {
  private lateinit var tripsRepository: TripsRepository
  private const val MAX_NOTIF_SIZE = 20

  fun initNotificationsManager(repository: TripsRepository) {
    tripsRepository = repository
  }

  private fun addNewNotification(
      notifList: MutableList<TripNotification>,
      newNotif: TripNotification
  ): List<TripNotification> {
    if (notifList.size >= MAX_NOTIF_SIZE) {
      notifList.removeLast()
    }
    notifList.add(0,newNotif)
    return notifList.toList()
  }

  suspend fun addJoinTripNotification(tripId: String) {
    var notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val newNotif =
        TripNotification(
            "${SessionManager.getCurrentUser()!!.name} joined the trip ",
            Route.MEMBERS,
            LocalDateTime.now())
    addNewNotification(notifList, newNotif)
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  suspend fun addCreateSuggestionNotification(tripId: String, suggestionId: String) {
    var notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val navActions = navigationActions.copy()
    navActions.setVariablesSuggestion(suggestionId)
    val newNotif =
        TripNotification(
            "${SessionManager.getCurrentUser()!!.name} created a new suggestion ",
            Route.SUGGESTION_DETAIL + "/" + navActions.serializeNavigationVariable(),
            LocalDateTime.now())
    addNewNotification(notifList, newNotif)
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }
}
