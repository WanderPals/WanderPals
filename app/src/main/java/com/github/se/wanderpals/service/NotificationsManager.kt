package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.NotificationAPI
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
   * Removes the path from a notification containing the provided expense ID.
   *
   * @param tripId The ID of the trip containing the notification.
   * @param expenseId The expense for which the notification path should be removed.
   */
  suspend fun removeExpensePath(tripId: String, expenseId: String) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()

    notifList.replaceAll {
      if (it.navActionVariables.contains("expenseId: $expenseId")) {
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
    broadcastNotification(
        tripId, "${SessionManager.getCurrentUser()!!.name} created a new suggestion ")

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
    val navActionVariables = ""
    val route = Route.STOPS_LIST
    val newNotif =
        TripNotification(
            "A new stop has been added for ${stop.date.format(
              DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy").withLocale(
                Locale.getDefault()))}",
            route,
            LocalDateTime.now(),
            navActionVariables)
    addNewNotification(notifList, newNotif)
    broadcastNotification(
        tripId,
        "A new stop has been added for ${stop.date.format(
      DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy").withLocale(
        Locale.getDefault()))}")
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  /**
   * Adds a notification for a new meeting at a given stop to the notification list of a trip.
   *
   * @param tripId The ID of the trip to which to add the notification.
   * @param stop The meeting stop for which to create the notification.
   */
  suspend fun addMeetingStopNotification(tripId: String, stop: Stop) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val navActions = navigationActions.copy()
    navActions.setVariablesLocation(stop.geoCords, stop.address)
    val newNotif =
        TripNotification(
            "${SessionManager.getCurrentUser()?.name} wants to meet at ${stop.geoCords.placeName}",
            Route.MAP,
            LocalDateTime.now(),
            navActions.serializeNavigationVariable())
    addNewNotification(notifList, newNotif)
    broadcastNotification(
        tripId,
        "${SessionManager.getCurrentUser()?.name} wants to meet at ${stop.geoCords.placeName}")
    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  /**
   * Adds a notification for a new expense to the notification list of a trip.
   *
   * @param tripId The ID of the trip to which to add the notification.
   * @param expense The added expense for which to create the notification.
   */
  suspend fun addExpenseNotification(tripId: String, expense: Expense) {
    val notifList = tripsRepository.getNotificationList(tripId).toMutableList()
    val navActions = navigationActions.copy()
    val route = Route.EXPENSE_INFO

    navActions.setVariablesExpense(expense)

    val navActionVariables = navActions.serializeNavigationVariable()

    val newNotif =
        TripNotification(
            "A new expense has been created", route, LocalDateTime.now(), navActionVariables)
    addNewNotification(notifList, newNotif)

    broadcastNotification(
        tripId, "A new expense has been created by ${SessionManager.getCurrentUser()!!.name}")

    tripsRepository.setNotificationList(tripId, notifList.toList())
  }

  private suspend fun broadcastNotification(tripId: String, message: String) {
    val tokenList = tripsRepository.getTrip(tripId)?.tokenIds
    if (tokenList != null) {
      NotificationAPI().sendNotification(tokenList, message)
    }
  }
}
