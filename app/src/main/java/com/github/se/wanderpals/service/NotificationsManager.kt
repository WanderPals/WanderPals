package com.github.se.wanderpals.service

import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.LocalDateTime

/**
 * Singleton object responsible for managing trip notifications.
 */
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
     * Adds a notification indicating that a user has joined a trip.
     *
     * @param tripId The ID of the trip for which the notification is added.
     */
    suspend fun addJoinTripNotification(tripId: String) {
        var notifList = tripsRepository.getNotificationList(tripId).toMutableList()
        val newNotif =
            TripNotification(
                "${SessionManager.getCurrentUser()!!.name} joined the trip ",
                Route.MEMBERS,
                LocalDateTime.now()
            )
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
        var notifList = tripsRepository.getNotificationList(tripId).toMutableList()
        val navActions = navigationActions.copy()
        navActions.setVariablesSuggestion(suggestionId)
        val newNotif =
            TripNotification(
                "${SessionManager.getCurrentUser()!!.name} created a new suggestion ",
                Route.SUGGESTION_DETAIL + "/" + navActions.serializeNavigationVariable(),
                LocalDateTime.now()
            )
        addNewNotification(notifList, newNotif)
        tripsRepository.setNotificationList(tripId, notifList.toList())
    }
}
