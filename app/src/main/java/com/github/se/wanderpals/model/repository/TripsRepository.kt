package com.github.se.wanderpals.model.repository

import FirestoreTrip
import android.util.Log
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.firestoreData.FirestoreStop
import com.github.se.wanderpals.model.firestoreData.FirestoreSuggestion
import com.github.se.wanderpals.model.firestoreData.FirestoreUser
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

  private lateinit var userRepository: UserRepository
  private lateinit var tripRepository: TripRepository
  private lateinit var expenseRepository: ExpenseRepository
  private lateinit var announcementRepository: AnnouncementRepository
  private lateinit var suggestionRepository: SuggestionRepository
  private lateinit var stopRepository: StopRepository
  private lateinit var notificationRepository: NotificationRepository
    private lateinit var tripIDsRepository: TripIDsRepository

  private lateinit var firestore: FirebaseFirestore
  // Reference to the 'Users' collection in Firestore
  private lateinit var usersCollection: CollectionReference
  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  private lateinit var usernameCollection: CollectionReference

  // private val notificationsId = "Notifications"
  private val balancesId = "Balances"

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

    userRepository = UserRepository(firestore, dispatcher, uid, this)
    tripRepository = TripRepository(firestore, dispatcher, uid, this)
    expenseRepository = ExpenseRepository(firestore, dispatcher, uid, this)
    announcementRepository = AnnouncementRepository(firestore, dispatcher, uid, this)
    suggestionRepository = SuggestionRepository(firestore, dispatcher, uid, this)
    stopRepository = StopRepository(firestore, dispatcher, uid, this)
    notificationRepository = NotificationRepository(firestore, dispatcher, uid, this)
      tripIDsRepository = TripIDsRepository(firestore, dispatcher, uid, this)
      userRepository.init()
    notificationRepository.init()
    expenseRepository.init()
    announcementRepository.init()
      suggestionRepository.init()
      stopRepository.init()
      tripIDsRepository.init()

    usersCollection = firestore.collection(FirebaseCollections.USERS_TO_TRIPS_IDS.path)
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
    usernameCollection = firestore.collection(FirebaseCollections.USERNAME_TO_EMAIL_COLLECTION.path)
    NotificationsManager.initNotificationsManager(this)
  }

  /**
   * Initializes Firestore with the default FirebaseApp. Sets up 'Users' and 'Trips' collection
   * references. Use for applications with a single Firebase project.
   */
  fun initFirestore() {
    Log.d("TripsRepository", "initFirestore: Initializing with default FirebaseApp.")
    firestore = FirebaseFirestore.getInstance()

    userRepository = UserRepository(firestore, dispatcher, uid, this)
    tripRepository = TripRepository(firestore, dispatcher, uid, this)
    expenseRepository = ExpenseRepository(firestore, dispatcher, uid, this)
    announcementRepository = AnnouncementRepository(firestore, dispatcher, uid, this)
    suggestionRepository = SuggestionRepository(firestore, dispatcher, uid, this)
    stopRepository = StopRepository(firestore, dispatcher, uid, this)
    notificationRepository = NotificationRepository(firestore, dispatcher, uid, this)
      tripIDsRepository = TripIDsRepository(firestore, dispatcher, uid, this)

    /*
         userRepository.init()
         tripRepository.init()
         expenseRepository.init()
         announcementRepository.init()
         suggestionRepository.init()
         stopRepository.init()

    */
      userRepository.init()
    notificationRepository.init()
    expenseRepository.init()
    announcementRepository.init()
      suggestionRepository.init()
      stopRepository.init()
      tripIDsRepository.init()

    usersCollection = firestore.collection(FirebaseCollections.USERS_TO_TRIPS_IDS.path)
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
    usernameCollection = firestore.collection(FirebaseCollections.USERNAME_TO_EMAIL_COLLECTION.path)
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

  open suspend fun getUserEmail(username: String): String? = userRepository.getUserEmail(username = username, source = getSource())

  open suspend fun addEmailToUsername(username: String, email: String): Boolean =if (checkNetworkIsValidAndLog())
      userRepository.addEmailToUsername(username = username, email = email, source = getSource())
       else false

  open suspend fun deleteEmailByUsername(username: String): Boolean =if (checkNetworkIsValidAndLog())
     userRepository.deleteEmailByUsername(username = username, source = getSource())
      else false

  open suspend fun getBalances(tripId: String): Map<String, Double> =
      expenseRepository.getBalances(tripId = tripId, source = getSource())

  open suspend fun setBalances(tripId: String, balancesMap: Map<String, Double>): Boolean =
      expenseRepository.setBalances(tripId = tripId, balancesMap = balancesMap)

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
  open suspend fun getSuggestionFromTrip(tripId: String, suggestionId: String): Suggestion? = suggestionRepository.getSuggestionFromTrip(tripId = tripId, suggestionId = suggestionId,source = getSource())

  open suspend fun getAllSuggestionsFromTrip(tripId: String): List<Suggestion> = suggestionRepository.getAllSuggestionsFromTrip(tripId = tripId, source = getSource())

  open suspend fun addSuggestionToTrip(tripId: String, suggestion: Suggestion): Boolean =if (checkNetworkIsValidAndLog())
      suggestionRepository.addSuggestionToTrip(tripId = tripId, suggestion = suggestion)
  else false

  open suspend fun removeSuggestionFromTrip(tripId: String, suggestionId: String): Boolean =if (checkNetworkIsValidAndLog())
      suggestionRepository.removeSuggestionFromTrip(tripId = tripId, suggestionId = suggestionId)
  else false

  open suspend fun updateSuggestionInTrip(tripId: String, suggestion: Suggestion): Boolean =if (checkNetworkIsValidAndLog())
      suggestionRepository.updateSuggestionInTrip(tripId = tripId, suggestion = suggestion)
  else false

    /**
     * Fetches a user's details for a specific trip. This method queries a subcollection within a trip
     * document to retrieve a user object based on the provided `userId`.
     *
     * @param tripId The unique identifier of the trip.
     * @param userId The unique identifier of the user.
     * @return A `User` object if found, `null` otherwise.
     */
  open suspend fun getUserFromTrip(tripId: String, userId: String): User? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.USERS_SUBCOLLECTION.path)
                  .document(userId)
                  .get(getSource())
                  .await()

          val firestoreUser = documentSnapshot.toObject<FirestoreUser>()
          if (firestoreUser != null) {
            firestoreUser.toUser()
          } else {
            Log.e("TripsRepository", "getUserFromTrip: Not found user $userId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getUserFromTrip: Error getting an user $userId from trip $tripId.",
              e)
          null // error
        }
      }

  /**
   * Retrieves all users associated with a specific trip. It iterates over all user IDs stored
   * within a trip document and fetches their corresponding user objects.
   *
   * @param tripId The unique identifier of the trip. appears unused and may be a mistake in the
   *   method signature.)
   * @return A list of `User` objects. Returns an empty list if the trip is not found or in case of
   *   an error.
   */
  open suspend fun getAllUsersFromTrip(tripId: String): List<User> =
      withContext(dispatcher) {
        try {
          val trip = getTrip(tripId)
          if (trip != null) {
            val stopIds = trip.users
            stopIds.mapNotNull { userId -> getUserFromTrip(tripId, userId) }
          } else {
            Log.e("TripsRepository", "getAllUsersFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "getAllUsersFromTrip: Error fetching trip to trip $tripId.", e)
          emptyList()
        }
      }

  /**
   * Adds a user to a specified trip. This method creates or updates a document in the Users
   * subcollection within a trip, storing the user's details.
   *
   * @param tripId The unique identifier of the trip.
   * @param user The `User` object containing the user's details.
   * @return `true` if the operation is successful, `false` otherwise.
   */
  open suspend fun addUserToTrip(tripId: String, user: User): Boolean =
      withContext(dispatcher) {
        try {
          if (!checkNetworkIsValidAndLog()) {
            return@withContext false
          }
          // for users, there IDs are google ids currently no need to gen a new one
          val firestoreUser = FirestoreUser.fromUser(user.copy(userId = uid))
          val userDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.USERS_SUBCOLLECTION.path)
                  .document(firestoreUser.userId)
          userDocument.set(firestoreUser).await()
          Log.d("TripsRepository", "addUserToTrip: User added successfully to trip $tripId.")
          val trip = getTrip(tripId)
          if (trip != null) {
            // Add the new userID to the trip's user list and update the trip
            val updatedStopsList = trip.users + user.userId

            val updatedTokensList = trip.tokenIds.toMutableList()
            if (!SessionManager.getNotificationToken().isEmpty()) {
              updatedTokensList += listOf(SessionManager.getNotificationToken())
              // problem when fetching the tokenIds
              // updatedTokensList = updatedStopsList + SessionManager.getNotificationToken()

            }
            val updatedTrip = trip.copy(users = updatedStopsList, tokenIds = updatedTokensList)
            updateTrip(updatedTrip)
            Log.d("TripsRepository", "addUserToTrip: Stop ID added to trip successfully.")
            true
          } else {

            Log.e("TripsRepository", "addUserToTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "addUserToTrip: Error adding user to trip $tripId.", e)
          false
        }
      }

  /**
   * Updates details of a user in a specified trip. Similar to `addUserToTrip`, but specifically
   * intended for updating existing user documents.
   *
   * @param tripId The unique identifier of the trip.
   * @param user The `User` object to be updated.
   * @return `true` if the operation is successful, `false` otherwise.
   */
  open suspend fun updateUserInTrip(tripId: String, user: User): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "updateUserInTrip: Updating a user in trip $tripId")
          if (!checkNetworkIsValidAndLog()) {
            return@withContext false
          }
          val firestoreUser = FirestoreUser.fromUser(user)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.USERS_SUBCOLLECTION.path)
              .document(user.userId)
              .set(firestoreUser)
              .await()
          Log.d(
              "TripsRepository",
              "updateUserInTrip: Trip's user updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateUserInTrip: Error updating user with ID ${user.userId} in trip with ID $tripId",
              e)
          false
        }
      }

  /**
   * Removes a user from a trip. This method deletes a user's document from the Users subcollection
   * within a trip. And the TripId from his accessible trips
   *
   * @param tripId The unique identifier of the trip.
   * @param userId The unique identifier of the user to be removed.
   * @return `true` if the operation is successful, `false` otherwise.
   */
  open suspend fun removeUserFromTrip(tripId: String, userId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "removeUserFromTrip: Deleting user $userId from trip $tripId")
          if (!checkNetworkIsValidAndLog()) {
            return@withContext false
          }
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.USERS_SUBCOLLECTION.path)
              .document(userId)
              .delete()
              .await()

          val trip = getTrip(tripId)
          if (trip != null) {
            val updatedUsersList = trip.users.filterNot { it == userId }
            var updatedTokensList = trip.tokenIds
            if (trip.tokenIds.contains(SessionManager.getNotificationToken())) {
              updatedTokensList = updatedTokensList - SessionManager.getNotificationToken()
            }
            val updatedTrip = trip.copy(users = updatedUsersList, tokenIds = updatedTokensList)

            updateTrip(updatedTrip)
            removeTripId(tripId, userId) // remove the Trip from the the deleted user
            Log.d(
                "TripsRepository",
                "removeUserFromTrip: User $userId deleted and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "removeUserFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeUserFromTrip: Error deleting user $userId from trip $tripId.",
              e)
          false
        }
      }

  open suspend fun getStopFromTrip(tripId: String, stopId: String): Stop? = stopRepository.getStopFromTrip(tripId = tripId, stopId = stopId, source = getSource())

  open suspend fun getAllStopsFromTrip(tripId: String): List<Stop> = stopRepository.getAllStopsFromTrip(tripId = tripId, source = getSource())

  open suspend fun addStopToTrip(tripId: String, stop: Stop): Boolean =if (checkNetworkIsValidAndLog())
      stopRepository.addStopToTrip(tripId = tripId, stop = stop)
  else false

  open suspend fun removeStopFromTrip(tripId: String, stopId: String): Boolean =if (checkNetworkIsValidAndLog())
      stopRepository.removeStopFromTrip(tripId = tripId, stopId = stopId)
  else false

  open suspend fun updateStopInTrip(tripId: String, stop: Stop): Boolean = if (checkNetworkIsValidAndLog())
      stopRepository.updateStopInTrip(tripId = tripId, stop = stop)
  else false

  /**
   * Asynchronously retrieves a trip by its ID from Firestore and converts it to the data model.
   *
   * @param tripId The unique identifier of the trip to retrieve.
   * @return The Trip object if found, null otherwise.
   */
  open suspend fun getTrip(tripId: String): Trip? =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "getTrip: Retrieving trip with ID $tripId.")
          val documentSnapshot = tripsCollection.document(tripId).get(getSource()).await()
          val firestoreTrip =
              documentSnapshot.toObject<
                  FirestoreTrip>() // Converts Firestore document to FirestoreTrip DTO
          firestoreTrip?.toTrip() // Converts FirestoreTrip DTO to Trip data model
        } catch (e: Exception) {
          Log.e("TripsRepository", "getTrip: Failed to fetch trip with ID $tripId", e)
          null // error
        }
      }

  /**
   * Fetches multiple trips by their IDs, leveraging the getTrip function for each ID.
   *
   * @return List of Trip objects.
   */
  open suspend fun getAllTrips(): List<Trip> =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "getAllTrips: Fetching all trips for user $uid.")
          val tripIds: List<String> = getTripsIds()
          tripIds.mapNotNull { tripId ->
            getTrip(tripId) // Utilizes the getTrip method to fetch each trip individually
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "getAllTrips: Failed to fetch all trips", e)
          emptyList() // error
        }
      }

  /**
   * Adds a new trip to Firestore, converting the Trip data model to FirestoreTrip DTO for storage.
   *
   * @param trip The Trip object to add.
   * @return Boolean indicating success or failure of the operation.
   */
  open suspend fun addTrip(trip: Trip): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "addTrip: Adding a trip")
          if (!checkNetworkIsValidAndLog()) {
            return@withContext false
          }

          // Generate a unique ID for the trip
          val uniqueID = UUID.randomUUID().toString()

          val firestoreTrip =
              FirestoreTrip.fromTrip(
                  trip.copy(tripId = uniqueID)) // Converts Trip data model to FirestoreTrip DTO
          tripsCollection
              .document(uniqueID)
              .set(firestoreTrip)
              .await() // Stores the FirestoreTrip DTO in Firestore
          addTripId(uniqueID, isOwner = true) // The creator of the Trip is automatic
          Log.d("TripsRepository", "addTrip: Trip added successfully with ID $uniqueID.")

          true
        } catch (e: Exception) {
          Log.e("TripsRepository", "addTrip: Error adding trip", e)
          false // error
        }
      }

  /**
   * Updates an existing trip in Firestore, utilizing the same conversion process as addTrip.
   *
   * @param trip The Trip object to update.
   * @return Boolean indicating success or failure of the operation.
   */
  open suspend fun updateTrip(trip: Trip): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "updateTrip: Updating a trip")
          if (!checkNetworkIsValidAndLog()) {
            return@withContext false
          }
          val firestoreTrip =
              FirestoreTrip.fromTrip(trip) // Converts Trip data model to FirestoreTrip DTO
          // Assuming tripId is already set in the trip object.
          tripsCollection
              .document(trip.tripId)
              .set(firestoreTrip)
              .await() // Stores the FirestoreTrip DTO in Firestore
          Log.d("TripsRepository", "updateTrip: Trip updated successfully for ID ${trip.tripId}.")

          true
        } catch (e: Exception) {
          Log.e("TripsRepository", "updateTrip: Error updating trip with ID ${trip.tripId}", e)
          false // error
        }
      }

  /**
   * Deletes a trip from Firestore based on its ID.
   *
   * @param tripId The unique identifier of the trip to delete.
   * @return Boolean indicating success or failure of the operation.
   */
  open suspend fun deleteTrip(tripId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "deleteTrip: Deleting trip")
          if (!checkNetworkIsValidAndLog()) {
            return@withContext false
          }

          val trip = getTrip(tripId)
          if (trip == null) {
            Log.d("TripsRepository", "deleteTrip: No trip found with ID $tripId")
            return@withContext false
          }

          coroutineScope {
            // Concurrently delete all components of the trip

            launch { setNotificationList(tripId, emptyList()) }
            trip.stops.forEach { stopId -> launch { removeStopFromTrip(tripId, stopId) } }
            trip.suggestions.forEach { suggestionId ->
              launch { removeSuggestionFromTrip(tripId, suggestionId) }
            }
            trip.announcements.forEach { announcementId ->
              launch { removeAnnouncementFromTrip(tripId, announcementId) }
            }
            trip.expenses.forEach { expenseId ->
              launch { removeExpenseFromTrip(tripId, expenseId) }
            }
            launch { setBalances(tripId, emptyMap()) }
            launch { removeTripId(tripId) }
            trip.users.forEach { userId -> launch { removeUserFromTrip(tripId, userId) } }

            launch { tripsCollection.document(tripId).delete().await() }
          }

          // delete a given trip by its tripId
          Log.d("TripsRepository", "deleteTrip: Trip deleted successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e("TripsRepository", "deleteTrip: Error deleting trip with ID $tripId", e)
          false // Handle error or log exception
        }
      }


  open suspend fun getTripsIds(): List<String> = tripIDsRepository.getTripsIds(source = getSource())
  open suspend fun addTripId(tripId: String, isOwner: Boolean = false): Boolean =if (checkNetworkIsValidAndLog())
      tripIDsRepository.addTripId(tripId = tripId, isOwner = isOwner, source = getSource())
  else false

  open suspend fun removeTripId(tripId: String, userId: String = uid): Boolean =if (checkNetworkIsValidAndLog())
      tripIDsRepository.removeTripId(tripId = tripId, userId = userId)
  else false
}
