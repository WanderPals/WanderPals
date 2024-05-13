package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.LocalDateTime

/** Singleton object responsible for managing trip notifications. */
object NotificationsManager {
  private lateinit var tripsRepository: TripsRepository
  private const val MAX_NOTIF_SIZE = 20

  /**
   * Initializes the NotificationsManager with the provided TripsRepository instance.
   *
   * @param repository The TripsRepository instance to be used for managing trip notifications.
   */
  fun initNotificationsManager(repository: TripsRepository) {
    tripsRepository = repository
  }

  /**
   * Adds a new notification to the list, maintaining a maximum size of [MAX_NOTIF_SIZE].
   *
   * @param notifList The current list of notifications.
   * @param newNotif The new notification to be added.
   * @return The updated list of notifications after adding the new one.
   */
  private fun addNewNotification(
      notifList: MutableList<TripNotification>,
      newNotif: TripNotification
  ): List<TripNotification> {
    if (notifList.size >= MAX_NOTIF_SIZE) {
      notifList.removeLast()
    }
    notifList.add(0, newNotif)
    return notifList.toList()
  }

  /**
   * Removes the path from a notification containing the provided suggestion ID.
   *
   * @param tripId The ID of the trip containing the notification.
   * @param suggestionId The suggestion for which the notification path should be removed.
   */
  suspend fun removeSuggestionPath(tripId: String, suggestionId: String) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()

    notifList.replaceAll {
      if (it.navActionVariables.contains("suggestionId: $suggestionId")) {
        it.copy(route = "", navActionVariables = "")
      } else {
        it
      }
    }
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  /**
   * Adds a notification indicating that a user has joined a trip.
   *
   * @param tripId The ID of the trip for which the notification is added.
   */
  suspend fun addJoinTripNotification(tripId: String) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val newNotif =
        TripNotification(
            "${SessionManager.getCurrentUser()!!.name} joined the trip ",
            Route.ADMIN_PAGE,
            LocalDateTime.now(),
            "")
    addNewNotification(notifList, newNotif)
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  /**
   * Adds a notification indicating that a new suggestion has been created for a trip.
   *
   * @param tripId The ID of the trip for which the notification is added.
   * @param suggestionId The ID of the newly created suggestion.
   */
  suspend fun addCreateSuggestionNotification(tripId: String, suggestionId: String) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val navActions = navigationActions.copy()
    navActions.setVariablesSuggestionId(suggestionId)
    val newNotif =
        TripNotification(
            "${SessionManager.getCurrentUser()!!.name} created a new suggestion ",
            Route.SUGGESTION_DETAIL,
            LocalDateTime.now(),
            navActions.serializeNavigationVariable())
    addNewNotification(notifList, newNotif)
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  /**
   * Adds a notification for a new stop to the notification list of a trip.
   *
   * @param tripId The ID of the trip to which to add the notification.
   * @param stop The added stop for which to create the notification.
   */
  suspend fun addStopNotification(tripId: String, stop: Stop) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val navActions = navigationActions.copy()
    var route = ""
    var navActionVariables = ""
    if (stop.address.isNotEmpty()) {
      navActions.setVariablesLocation(geoCords = stop.geoCords, address = stop.address)
      route = Route.MAP
      navActionVariables = navActions.serializeNavigationVariable()
    }
    val newNotif =
        TripNotification(
            "A new stop has been added ", route, LocalDateTime.now(), navActionVariables)
    addNewNotification(notifList, newNotif)
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  /**
   * Adds a notification for a new expense to the notification list of a trip.
   *
   * @param tripId The ID of the trip to which to add the notification.
   * @param expense The added expense  for which to create the notification.
   */
  suspend fun addExpenseNotification(tripId: String, expense: Expense) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val navActions = navigationActions.copy()
    var route = Route.EXPENSE_INFO

    navActions.setVariablesExpense(expense)

    var navActionVariables = navActions.serializeNavigationVariable()

    val newNotif =
      TripNotification(
        "A new expense has been created", route, LocalDateTime.now(), navActionVariables)
    addNewNotification(notifList, newNotif)

    tripsRepository.setNotificationList(tripId, notifList.toList())
  }
}
