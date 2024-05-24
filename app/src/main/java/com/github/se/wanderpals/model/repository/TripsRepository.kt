package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.service.NotificationsManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provides an interface for accessing and manipulating trip data stored in Firestore. Utilizes
 * Firestore's async APIs within Kotlin coroutines for non-blocking IO operations. This repository
 * abstracts away Firestore-specific details, offering a clean data model interface to the rest of
 * the application.
 *
 * @param uid Unique identifier of the current user. Used to fetch user-specific trip data.(UsersId
 *   in short)
 */
open class TripsRepository(
    var uid: String,
    private val dispatcher: CoroutineDispatcher // Inject dispatcher
) {
  private val userRepository: UserRepository = UserRepository(dispatcher, uid, this)
  private val tripRepository: TripRepository = TripRepository(dispatcher, uid, this)
  private val expenseRepository: ExpenseRepository = ExpenseRepository(dispatcher, uid, this)
  private val announcementRepository: AnnouncementRepository =
      AnnouncementRepository(dispatcher, uid, this)
  private val suggestionRepository: SuggestionRepository =
      SuggestionRepository(dispatcher, uid, this)
  private val stopRepository: StopRepository = StopRepository(dispatcher, uid, this)
  private val notificationRepository: NotificationRepository =
      NotificationRepository(dispatcher, uid, this)
  private val tripIDsRepository: TripIDsRepository = TripIDsRepository(dispatcher, uid, this)

  private lateinit var firestore: FirebaseFirestore

  var isNetworkEnabled = false // by default we consider that this is false

  /**
   * (Currently used only for unit tests)
   *
   * Initializes Firestore with a specified FirebaseApp. Sets up 'Users' and 'Trips' collection
   * references. Use when working with a specific FirebaseApp instance.
   *
   * @param app The FirebaseApp instance for Firestore initialization.
   */
  fun initFirestore(app: FirebaseApp) {
    Log.d("TripsRepository", "initFirestore(app): Initializing with specific FirebaseApp instance.")
    firestore = FirebaseFirestore.getInstance(app)
    userRepository.init(firestore)
    notificationRepository.init(firestore)
    expenseRepository.init(firestore)
    announcementRepository.init(firestore)
    suggestionRepository.init(firestore)
    stopRepository.init(firestore)
    tripIDsRepository.init(firestore)
    tripRepository.init(firestore)

    NotificationsManager.initNotificationsManager(this)
  }

  /**
   * Initializes Firestore with the default FirebaseApp. Sets up 'Users' and 'Trips' collection
   * references. Use for applications with a single Firebase project.
   */
  fun initFirestore() {
    Log.d("TripsRepository", "initFirestore: Initializing with default FirebaseApp.")
    firestore = FirebaseFirestore.getInstance()
    userRepository.init(firestore)
    notificationRepository.init(firestore)
    expenseRepository.init(firestore)
    announcementRepository.init(firestore)
    suggestionRepository.init(firestore)
    stopRepository.init(firestore)
    tripIDsRepository.init(firestore)
    tripRepository.init(firestore)

    NotificationsManager.initNotificationsManager(this)
  }

  /**
   * Retrieves the appropriate Firestore data source based on network availability: Source.DEFAULT
   * if online, Source.CACHE if offline.
   *
   * @return Source The data source setting, either DEFAULT for online or CACHE for offline access.
   */
  private fun getSource(): Source = if (isNetworkEnabled) Source.DEFAULT else Source.CACHE

  /**
   * Checks network availability and logs if the device is offline, useful for aborting operations
   * when no network.
   *
   * @return Boolean True if network is available, false if offline.
   */
  private fun checkNetworkIsValidAndLog(): Boolean {
    if (!isNetworkEnabled) {
      Log.d("FirebaseRepository", "Operation is aborted, device is offline")
    }
    return isNetworkEnabled
  }

  /**
   * ********************************************************************************************
   * Notification Methods
   * *********************************************************************************************
   */
  open suspend fun getNotificationList(tripId: String): List<TripNotification> =
      notificationRepository.getNotificationList(tripId = tripId, source = getSource())

  open suspend fun setNotificationList(
      tripId: String,
      notifications: List<TripNotification>
  ): Boolean =
      if (checkNetworkIsValidAndLog())
          notificationRepository.setNotificationList(
              tripId = tripId, notifications = notifications, source = getSource())
      else false

  /**
   * ********************************************************************************************
   * Finance Methods
   * *********************************************************************************************
   */
  open suspend fun getBalances(tripId: String): Map<String, Double> =
      expenseRepository.getBalances(tripId = tripId, source = getSource())

  open suspend fun setBalances(tripId: String, balancesMap: Map<String, Double>): Boolean =
      expenseRepository.setBalances(tripId = tripId, balancesMap = balancesMap)

  open suspend fun getExpenseFromTrip(tripId: String, expenseId: String): Expense? =
      expenseRepository.getExpenseFromTrip(
          tripId = tripId, expenseId = expenseId, source = getSource())

  open suspend fun getAllExpensesFromTrip(tripId: String): List<Expense> =
      expenseRepository.getAllExpensesFromTrip(tripId = tripId, source = getSource())

  open suspend fun addExpenseToTrip(tripId: String, expense: Expense): String =
      if (checkNetworkIsValidAndLog())
          expenseRepository.addExpenseToTrip(tripId = tripId, expense = expense)
      else ""

  open suspend fun removeExpenseFromTrip(tripId: String, expenseId: String): Boolean =
      if (checkNetworkIsValidAndLog())
          expenseRepository.removeExpenseFromTrip(tripId = tripId, expenseId = expenseId)
      else false

  open suspend fun updateExpenseInTrip(tripId: String, expense: Expense): Boolean =
      if (checkNetworkIsValidAndLog())
          expenseRepository.updateExpenseInTrip(tripId = tripId, expense = expense)
      else false

  /**
   * ********************************************************************************************
   * Announcements Methods
   * *********************************************************************************************
   */
  open suspend fun getAnnouncementFromTrip(tripId: String, announcementId: String): Announcement? =
      announcementRepository.getAnnouncementFromTrip(
          tripId = tripId, announcementId = announcementId, source = getSource())

  open suspend fun getAllAnnouncementsFromTrip(tripId: String): List<Announcement> =
      announcementRepository.getAllAnnouncementsFromTrip(tripId = tripId, source = getSource())

  open suspend fun addAnnouncementToTrip(tripId: String, announcement: Announcement): Boolean =
      if (checkNetworkIsValidAndLog())
          announcementRepository.addAnnouncementToTrip(tripId = tripId, announcement = announcement)
      else false

  open suspend fun removeAnnouncementFromTrip(tripId: String, announcementId: String): Boolean =
      if (checkNetworkIsValidAndLog())
          announcementRepository.removeAnnouncementFromTrip(
              tripId = tripId, announcementId = announcementId)
      else false

  open suspend fun updateAnnouncementInTrip(tripId: String, announcement: Announcement): Boolean =
      if (checkNetworkIsValidAndLog())
          announcementRepository.updateAnnouncementInTrip(
              tripId = tripId, announcement = announcement)
      else false

  /**
   * ********************************************************************************************
   * Suggestions Methods
   * *********************************************************************************************
   */
  open suspend fun getSuggestionFromTrip(tripId: String, suggestionId: String): Suggestion? =
      suggestionRepository.getSuggestionFromTrip(
          tripId = tripId, suggestionId = suggestionId, source = getSource())

  open suspend fun getAllSuggestionsFromTrip(tripId: String): List<Suggestion> =
      suggestionRepository.getAllSuggestionsFromTrip(tripId = tripId, source = getSource())

  open suspend fun addSuggestionToTrip(tripId: String, suggestion: Suggestion): Boolean =
      if (checkNetworkIsValidAndLog())
          suggestionRepository.addSuggestionToTrip(tripId = tripId, suggestion = suggestion)
      else false

  open suspend fun removeSuggestionFromTrip(tripId: String, suggestionId: String): Boolean =
      if (checkNetworkIsValidAndLog())
          suggestionRepository.removeSuggestionFromTrip(
              tripId = tripId, suggestionId = suggestionId)
      else false

  open suspend fun updateSuggestionInTrip(tripId: String, suggestion: Suggestion): Boolean =
      if (checkNetworkIsValidAndLog())
          suggestionRepository.updateSuggestionInTrip(tripId = tripId, suggestion = suggestion)
      else false

  /**
   * ********************************************************************************************
   * Users Methods
   * *********************************************************************************************
   */
  open suspend fun getUserEmail(username: String): String? =
      userRepository.getUserEmail(username = username, source = getSource())

  open suspend fun addEmailToUsername(username: String, email: String): Boolean =
      if (checkNetworkIsValidAndLog())
          userRepository.addEmailToUsername(
              username = username, email = email, source = getSource())
      else false

  open suspend fun deleteEmailByUsername(username: String): Boolean =
      if (checkNetworkIsValidAndLog())
          userRepository.deleteEmailByUsername(username = username, source = getSource())
      else false

  open suspend fun getUserFromTrip(tripId: String, userId: String): User? =
      userRepository.getUserFromTrip(tripId = tripId, userId = userId, source = getSource())

  open suspend fun getAllUsersFromTrip(tripId: String): List<User> =
      userRepository.getAllUsersFromTrip(tripId = tripId, source = getSource())

  open suspend fun addUserToTrip(tripId: String, user: User): Boolean =
      if (checkNetworkIsValidAndLog()) userRepository.addUserToTrip(tripId = tripId, user = user)
      else false

  open suspend fun updateUserInTrip(tripId: String, user: User): Boolean =
      if (checkNetworkIsValidAndLog()) userRepository.updateUserInTrip(tripId = tripId, user = user)
      else false

  open suspend fun removeUserFromTrip(tripId: String, userId: String): Boolean =
      if (checkNetworkIsValidAndLog())
          userRepository.removeUserFromTrip(tripId = tripId, userId = userId)
      else false

  /**
   * ********************************************************************************************
   * Stops Methods
   * *********************************************************************************************
   */
  open suspend fun getStopFromTrip(tripId: String, stopId: String): Stop? =
      stopRepository.getStopFromTrip(tripId = tripId, stopId = stopId, source = getSource())

  open suspend fun getAllStopsFromTrip(tripId: String): List<Stop> =
      stopRepository.getAllStopsFromTrip(tripId = tripId, source = getSource())

  open suspend fun addStopToTrip(tripId: String, stop: Stop): Boolean =
      if (checkNetworkIsValidAndLog()) stopRepository.addStopToTrip(tripId = tripId, stop = stop)
      else false

  open suspend fun removeStopFromTrip(tripId: String, stopId: String): Boolean =
      if (checkNetworkIsValidAndLog())
          stopRepository.removeStopFromTrip(tripId = tripId, stopId = stopId)
      else false

  open suspend fun updateStopInTrip(tripId: String, stop: Stop): Boolean =
      if (checkNetworkIsValidAndLog()) stopRepository.updateStopInTrip(tripId = tripId, stop = stop)
      else false

  /**
   * ********************************************************************************************
   * Trips Methods
   * *********************************************************************************************
   */
  open suspend fun getTrip(tripId: String): Trip? =
      tripRepository.getTrip(tripId = tripId, source = getSource())

  open suspend fun getAllTrips(): List<Trip> = tripRepository.getAllTrips(source = getSource())

  open suspend fun addTrip(trip: Trip): Boolean =
      if (checkNetworkIsValidAndLog()) tripRepository.addTrip(trip = trip) else false

  open suspend fun updateTrip(trip: Trip): Boolean =
      if (checkNetworkIsValidAndLog()) tripRepository.updateTrip(trip = trip) else false

  open suspend fun deleteTrip(tripId: String): Boolean =
      if (checkNetworkIsValidAndLog()) tripRepository.deleteTrip(tripId = tripId) else false

  /**
   * ********************************************************************************************
   * UserTripIds Methods
   * *********************************************************************************************
   */
  open suspend fun getTripsIds(): List<String> = tripIDsRepository.getTripsIds(source = getSource())

  open suspend fun addTripId(tripId: String, isOwner: Boolean = false): Boolean =
      if (checkNetworkIsValidAndLog())
          tripIDsRepository.addTripId(tripId = tripId, isOwner = isOwner, source = getSource())
      else false

  open suspend fun removeTripId(tripId: String, userId: String = uid): Boolean =
      if (checkNetworkIsValidAndLog())
          tripIDsRepository.removeTripId(tripId = tripId, userId = userId)
      else false
}
