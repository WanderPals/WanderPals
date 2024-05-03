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
import com.github.se.wanderpals.model.firestoreData.FirestoreAnnouncement
import com.github.se.wanderpals.model.firestoreData.FirestoreExpense
import com.github.se.wanderpals.model.firestoreData.FirestoreStop
import com.github.se.wanderpals.model.firestoreData.FirestoreSuggestion
import com.github.se.wanderpals.model.firestoreData.FirestoreTripNotification
import com.github.se.wanderpals.model.firestoreData.FirestoreUser
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
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
    val uid: String,
    private val dispatcher: CoroutineDispatcher // Inject dispatcher
) {

  private lateinit var firestore: FirebaseFirestore
  // Reference to the 'Users' collection in Firestore
  private lateinit var usersCollection: CollectionReference
  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  private lateinit var usernameCollection: CollectionReference

  private val notificationsId = "Notifications"
  private val balancesId = "Balances"

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
    usersCollection = firestore.collection(FirebaseCollections.USERS_TO_TRIPS_IDS.path)
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
    usernameCollection = firestore.collection(FirebaseCollections.USERNAME_TO_EMAIL_COLLECTION.path)
    NotificationsManager.initNotificationsManager(this)
  }

  /**
   * Retrieves the email associated with a specific username from Firestore. This method queries the
   * Firestore 'usernameCollection' for a document matching the provided username. If the document
   * exists, it retrieves the 'email' field from the document.
   *
   * @param username The username for which the email is to be fetched.
   * @return The email associated with the username if found, or null if no such username exists or
   *   an error occurs.
   */
  open suspend fun getUserEmail(username: String): String? =
      withContext(dispatcher) {
        try {
          val documentSnapshot = usernameCollection.document(username).get().await()
          // Attempt to retrieve the list using the correct type information
          documentSnapshot.data?.get("email") as? String
        } catch (e: Exception) {
          Log.e(
              "TripsRepository", "getUserEmail: Error getting the email for username $username.", e)
          null
        }
      }

  /**
   * Adds a new username and email pair to Firestore. This method first checks if a document with
   * the specified username already exists in the 'usernameCollection'. If it exists, the addition
   * is aborted and false is returned to indicate failure. If it does not exist, a new document with
   * the username as the key and email as the value is created.
   *
   * @param username The username to be added.
   * @param email The email to be associated with the username.
   * @return True if the email was successfully added, false if the username already exists or an
   *   error occurs.
   */
  open suspend fun addEmailToUsername(username: String, email: String): Boolean =
      withContext(dispatcher) {
        try {
          val documentRef = usernameCollection.document(username)
          val snapshot = documentRef.get().await()

          if (snapshot.exists()) {
            Log.e("TripsRepository", "addEmailToUsername: Username $username already exists.")
            return@withContext false
          }

          documentRef.set(mapOf("email" to email)).await()
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "addEmailToUsername: Error adding email for username $username.",
              e)
          false
        }
      }

  /**
   * Deletes an email associated with a specific username from Firestore. This method first checks
   * if a document for the specified username exists in the 'usernameCollection'. If the document
   * does not exist, it returns true as the end state (username not present) is already achieved. If
   * the document exists, it proceeds to delete it. If deletion is successful, true is returned.
   *
   * @param username The username whose associated email is to be deleted.
   * @return True if the deletion was successful or the username did not exist, false if an error
   *   occurs during deletion.
   */
  open suspend fun deleteEmailByUsername(username: String): Boolean =
      withContext(dispatcher) {
        try {
          val documentRef = usernameCollection.document(username)
          val snapshot = documentRef.get().await()

          // Check if the document exists. If it does not, log for information but return true
          // anyway.
          if (!snapshot.exists()) {
            Log.i(
                "TripsRepository",
                "deleteEmailByUsername: Username $username does not exist, no need to delete.")
            return@withContext true
          }
          // Proceed with the deletion if the document exists.
          documentRef.delete().await()
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "deleteEmailByUsername: Error deleting email for username $username.",
              e)
          false
        }
      }

  /**
   * Retrieves the balance map for a specific trip from Firestore. This method queries the Firestore
   * subcollection for balances associated with a given trip document identified by its unique trip
   * ID. The balances are stored under a specific document, which is predefined by `balancesId`.
   *
   * @param tripId The unique identifier of the trip for which balances are to be fetched.
   * @return A map of String to Double representing the balances for the trip. Returns an empty map
   *   if the balances document is not found, if there are no balances in the document, or in case
   *   of an error during data retrieval. Errors during the operation are logged and an empty map is
   *   returned.
   */
  open suspend fun getBalances(tripId: String): Map<String, Double> =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_BALANCES_SUBCOLLECTION.path)
                  .document(balancesId)
                  .get()
                  .await()
          @Suppress("UNCHECKED_CAST")
          (documentSnapshot.data as? Map<String, Double>) ?: emptyMap()
        } catch (e: Exception) {
          Log.e("TripsRepository", "getBalances: Error getting balance map for trip $tripId", e)
          emptyMap()
        }
      }

  /**
   * Sets or updates the balance map for a specific trip in Firestore. This method updates the
   * Firestore subcollection for balances by setting a map of String to Double associated with a
   * given trip document, identified by its unique trip ID. The balances are stored under a specific
   * document predefined by `balancesId`.
   *
   * @param tripId The unique identifier of the trip for which balances are to be set.
   * @param balancesMap The map of String to Double to be stored as balances for the trip.
   * @return A Boolean value indicating the success or failure of the operation. Returns `true` if
   *   the operation is successful, and `false` if there is an error during the setting operation.
   *   Errors are logged.
   */
  open suspend fun setBalances(tripId: String, balancesMap: Map<String, Double>): Boolean =
      withContext(dispatcher) {
        try {
          val balanceDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_BALANCES_SUBCOLLECTION.path)
                  .document(balancesId)

          if (balancesMap.isEmpty()) {
            balanceDocument.delete().await()
          } else {
            balanceDocument.set(balancesMap).await()
          }
          true
        } catch (e: Exception) {
          Log.e("TripsRepository", "setBalances: Error setting balance map for trip $tripId", e)
          false
        }
      }

  /**
   * Retrieves a list of notifications for a specific trip from Firestore. This method queries the
   * Firestore subcollection for notifications associated with a given trip document identified by
   * its unique trip ID. The notifications are stored under a specific document, which is
   * predefined.
   *
   * @param tripId The unique identifier of the trip for which notifications are to be fetched.
   * @return A list of `TripNotification` objects, representing the notifications for the trip.
   *   Returns an empty list if the notifications document is not found, if there are no
   *   notifications in the document, or in case of an error during data retrieval. Errors during
   *   the operation are logged and an empty list is returned.
   */
  open suspend fun getNotificationList(tripId: String): List<TripNotification> =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_NOTIFICATIONS_SUBCOLLECTION.path)
                  .document(notificationsId)
                  .get()
                  .await()
          // Attempt to retrieve the list using the correct type information
          val data = documentSnapshot.data
          @Suppress("UNCHECKED_CAST")
          val notificationsList =
              data?.get(notificationsId) as? List<Map<String, Any>> ?: emptyList()

          // Convert the list of map to list of FirestoreTripNotification
          notificationsList.map { map ->
            FirestoreTripNotification(
                    title = map["title"] as String,
                    route = map["route"] as String,
                    timestamp = map["timestamp"] as String,
                    navActionVariables = map["navActionVariables"] as String)
                .toTripNotification()
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getNotificationList: Error getting the Notification List for Trip $tripId.",
              e)
          emptyList()
        }
      }

  /**
   * Sets or updates a list of notifications for a specific trip in Firestore. This method updates
   * the Firestore subcollection for notifications by setting a list of notifications associated
   * with a given trip document, identified by its unique trip ID. The notifications are stored
   * under a specific document predefined by `notificationsId`.
   *
   * @param tripId The unique identifier of the trip for which notifications are to be set.
   * @param notifications The list of `TripNotification` objects to be stored as notifications for
   *   the trip.
   * @return A Boolean value indicating the success or failure of the operation. Returns `true` if
   *   the operation is successful, and `false` if there is an error during the setting operation.
   *   Errors are logged.
   */
  open suspend fun setNotificationList(
      tripId: String,
      notifications: List<TripNotification>
  ): Boolean =
      withContext(dispatcher) {
        try {

          val notificationDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_NOTIFICATIONS_SUBCOLLECTION.path)
                  .document(notificationsId)

          if (notifications.isEmpty()) {
            notificationDocument.delete().await()
            true
          } else {
            // Convert all TripNotifications to FirestoreTripNotifications
            val firestoreNotificationList =
                notifications.map { FirestoreTripNotification.fromTripNotification(it) }
            // Set the notifications list in Firestore using the predefined notificationsId key
            notificationDocument.set(mapOf(notificationsId to firestoreNotificationList)).await()
            true
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "setNotificationList: Error setting the Notification List for Trip $tripId.",
              e)
          false
        }
      }

  /**
   * Retrieves a specific expense from a trip using the expense's unique identifier. This method
   * queries the Firestore subcollection for trip expenses within a specific trip document.
   *
   * @param tripId The unique identifier of the trip.
   * @param expenseId The unique identifier of the expense to be retrieved.
   * @return An `Expense` object if found, or `null` if the expense is not found or if an error
   *   occurs. The method logs an error and returns `null` in case of failure.
   */
  open suspend fun getExpenseFromTrip(tripId: String, expenseId: String): Expense? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
                  .document(expenseId)
                  .get()
                  .await()
          val firestoreExpense = documentSnapshot.toObject<FirestoreExpense>()
          if (firestoreExpense != null) {
            firestoreExpense.toExpense()
          } else {
            Log.e(
                "TripsRepository",
                "getExpenseFromTrip: Not found Expense $expenseId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getExpenseFromTrip: Error getting Expense $expenseId from trip $tripId.",
              e)
          null // error
        }
      }

  /**
   * Fetches all expenses associated with a specific trip. This method retrieves an array of
   * expenses by leveraging the `getExpenseFromTrip` method for each expense ID found in the trip
   * document.
   *
   * @param tripId The unique identifier of the trip for which expenses are being retrieved.
   * @return A list of `Expense` objects. Returns an empty list if the trip is not found or if an
   *   error occurs during fetching. Errors are logged and an empty list is returned in these cases.
   */
  open suspend fun getAllExpensesFromTrip(tripId: String): List<Expense> =
      withContext(dispatcher) {
        try {
          val trip = getTrip(tripId)

          if (trip != null) {
            val expenseIds = trip.expenses
            expenseIds.mapNotNull { expenseId -> getExpenseFromTrip(tripId, expenseId) }
          } else {
            Log.e("TripsRepository", "getAllExpensesFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e(
              "TripsRepository",
              "getAllExpensesFromTrip: Error fetching Expenses to trip $tripId.",
              e)
          emptyList()
        }
      }

  /**
   * Adds a new expense to a specified trip. This method generates a unique identifier for the new
   * expense, stores it in the Firestore, and updates the trip document to include this new expense
   * ID.
   *
   * @param tripId The unique identifier of the trip to which the expense is being added.
   * @param expense The `Expense` object to be added.
   * @return The unique identifier of the newly added expense if the operation is successful, or an
   *   empty string if it fails. The method logs the outcome of the operation.
   */
  open suspend fun addExpenseToTrip(tripId: String, expense: Expense): String =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString()
          val firebaseExpense =
              FirestoreExpense.fromExpense(
                  expense.copy(
                      expenseId = uniqueID,
                      userId = uid)) // we already know who creates the Expense
          val expenseDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
                  .document(uniqueID)
          expenseDocument.set(firebaseExpense).await()
          Log.d("TripsRepository", "addExpenseToTrip: Expense added successfully to trip $tripId.")

          val trip = getTrip(tripId)
          if (trip != null) {
            // Add the new ExpenseId to the trip's Expense list and update the
            // trip
            val updatedExpensesList = trip.expenses + uniqueID
            val updatedTrip = trip.copy(expenses = updatedExpensesList)
            updateTrip(updatedTrip)
            Log.d("TripsRepository", "addExpenseToTrip: Expense ID added to trip successfully.")

            uniqueID // this function will return the new object ID if its successful
          } else {

            Log.e("TripsRepository", "addExpenseToTrip: Trip not found with ID $tripId.")

            "" // And will return an empty string in case of failure
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "addExpenseToTrip: Error adding Expense to trip $tripId.", e)
          ""
        }
      }

  /**
   * Removes a specific expense from a trip. This method deletes the expense document from Firestore
   * and updates the trip's expense list to exclude the removed expense ID.
   *
   * @param tripId The unique identifier of the trip.
   * @param expenseId The unique identifier of the expense to be removed.
   * @return `true` if the expense is successfully removed and the trip is updated; `false` if the
   *   trip is not found or if any error occurs during the operation. Errors are logged.
   */
  open suspend fun removeExpenseFromTrip(tripId: String, expenseId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "removeExpenseFromTrip: removing Expense $expenseId from trip $tripId")
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
              .document(expenseId)
              .delete()
              .await()

          val trip = getTrip(tripId)
          if (trip != null) {
            val updatedExpensesList = trip.expenses.filterNot { it == expenseId }
            val updatedTrip = trip.copy(expenses = updatedExpensesList)
            updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "removeExpenseFromTrip: Expense $expenseId remove and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "removeExpenseFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeExpenseFromTrip: Error removing Expense $expenseId from trip $tripId.",
              e)
          false
        }
      }

  /**
   * Updates an existing expense within a trip. This method replaces the current expense document in
   * Firestore with the updated expense data provided.
   *
   * @param tripId The unique identifier of the trip containing the expense.
   * @param expense The updated `Expense` object.
   * @return `true` if the expense is successfully updated; `false` if the trip is not found or an
   *   error occurs. Errors are logged and the method returns `false` in these cases.
   */
  open suspend fun updateExpenseInTrip(tripId: String, expense: Expense): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "updateExpenseInTrip: Updating a Expense in trip $tripId")
          val firestoreExpense = FirestoreExpense.fromExpense(expense)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.TRIP_EXPENSES_SUBCOLLECTION.path)
              .document(firestoreExpense.expenseId)
              .set(firestoreExpense)
              .await()
          Log.d(
              "TripsRepository",
              "updateExpenseInTrip: Trip's Expense updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateExpenseInTrip: Error updating Expense with ID ${expense.expenseId} in trip with ID $tripId",
              e)
          false
        }
      }

  /**
   * Retrieves a specific trip Announcement from a trip based on the Announcement's unique
   * identifier. This method queries the Firestore subcollection for trip Announcements within a
   * specific trip document.
   *
   * @param tripId The unique identifier of the trip.
   * @param announcementId The unique identifier of the trip Announcement to be retrieved.
   * @return A `Announcement` object if found, or `null` if the Announcement is not found or if an
   *   error occurs. The method logs an error and returns `null` in case of failure.
   */
  open suspend fun getAnnouncementFromTrip(tripId: String, announcementId: String): Announcement? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
                  .document(announcementId)
                  .get()
                  .await()
          val firestoreAnnouncement = documentSnapshot.toObject<FirestoreAnnouncement>()
          if (firestoreAnnouncement != null) {
            firestoreAnnouncement.toAnnouncement()
          } else {
            Log.e(
                "TripsRepository",
                "getAnnouncementFromTrip: Not found Announcement $announcementId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getAnnouncementFromTrip: Error getting a Announcement $announcementId from trip $tripId.",
              e)
          null // error
        }
      }

  /**
   * Retrieves all trip Announcements associated with a specific trip. It iterates over all
   * Announcement IDs stored within the trip document and fetches their corresponding trip
   * Announcement objects.
   *
   * @param tripId The unique identifier of the trip.
   * @return A list of `Announcement` objects. Returns an empty list if the trip is not found, if
   *   there are no Announcements associated with the trip, or in case of an error during data
   *   retrieval.
   */
  open suspend fun getAllAnnouncementsFromTrip(tripId: String): List<Announcement> =
      withContext(dispatcher) {
        try {
          val trip = getTrip(tripId)

          if (trip != null) {
            val announcementIds = trip.announcements
            announcementIds.mapNotNull { announcementId ->
              getAnnouncementFromTrip(tripId, announcementId)
            }
          } else {
            Log.e("TripsRepository", "getAllAnnouncementsFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e(
              "TripsRepository",
              "getAllAnnouncementsFromTrip: Error fetching Announcement to trip $tripId.",
              e)
          emptyList()
        }
      }

  /**
   * Adds a Announcement to a specific trip in the Firestore database. This method generates a
   * unique ID for the new Announcement, creates a corresponding FirestoreAnnouncement object, and
   * commits it to the trip's Announcements subcollection.
   *
   * It also updates the trip's document to include this new Announcement ID in its list.
   *
   * @param tripId The unique identifier of the trip where the Announcement will be added.
   * @param announcement The Announcement object to add.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  open suspend fun addAnnouncementToTrip(tripId: String, announcement: Announcement): Boolean =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString()
          val firebaseAnnouncement =
              FirestoreAnnouncement.fromAnnouncement(
                  announcement.copy(
                      announcementId = uniqueID,
                      userId = uid)) // we already know who creates the Announcement
          val announcementDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
                  .document(uniqueID)
          announcementDocument.set(firebaseAnnouncement).await()
          Log.d(
              "TripsRepository",
              "addAnnouncementToTrip: Announcement added successfully to trip $tripId.")

          val trip = getTrip(tripId)
          if (trip != null) {
            // Add the new AnnouncementId to the trip's Announcements list and update the
            // trip
            val updatedAnnouncementsList = trip.announcements + uniqueID
            val updatedTrip = trip.copy(announcements = updatedAnnouncementsList)
            updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "addAnnouncementToTrip: Announcement ID added to trip successfully.")
            true
          } else {

            Log.e("TripsRepository", "addAnnouncementToTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "addAnnouncementToTrip: Error adding Announcement to trip $tripId.",
              e)
          false
        }
      }

  /**
   * Removes a specific Announcement from a trip's Announcement list and the Firestore database.
   * This method first deletes the Announcement document from the trip's Announcement subcollection.
   *
   * It also updates the trip's document to remove the Announcement ID from its list.
   *
   * @param tripId The unique identifier of the trip.
   * @param announcementId The unique identifier of the Announcement to be removed.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  open suspend fun removeAnnouncementFromTrip(tripId: String, announcementId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "removeAnnouncementFromTrip: removing Announcement $announcementId from trip $tripId")
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
              .document(announcementId)
              .delete()
              .await()

          val trip = getTrip(tripId)
          if (trip != null) {
            val updatedAnnouncementsList = trip.announcements.filterNot { it == announcementId }
            val updatedTrip = trip.copy(announcements = updatedAnnouncementsList)
            updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "removeAnnouncementFromTrip: Announcement $announcementId remove and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "removeAnnouncementFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeAnnouncementFromTrip: Error removing Announcement $announcementId from trip $tripId.",
              e)
          false
        }
      }

  /**
   * Updates a specific Announcement in a trip's Announcement subcollection in the Firestore
   * database. This method creates a FirestoreAnnouncement object from the provided Announcement
   * object and sets it to the corresponding document identified by the Announcement ID.
   *
   * @param tripId The unique identifier of the trip.
   * @param announcement The Announcement object that contains updated data.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  open suspend fun updateAnnouncementInTrip(tripId: String, announcement: Announcement): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "updateAnnouncementInTrip: Updating a Announcement in trip $tripId")
          val firestoreAnnouncement = FirestoreAnnouncement.fromAnnouncement(announcement)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
              .document(firestoreAnnouncement.announcementId)
              .set(firestoreAnnouncement)
              .await()
          Log.d(
              "TripsRepository",
              "updateAnnouncementInTrip: Trip's Announcement updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateAnnouncementInTrip: Error updating Announcement with ID ${announcement.announcementId} in trip with ID $tripId",
              e)
          false
        }
      }

  /**
   * Retrieves a specific suggestion from a trip based on the suggestion's unique identifier. This
   * method queries a subcollection within a trip document to retrieve a suggestion object based on
   * the provided `suggestionId`.
   *
   * @param tripId The unique identifier of the trip.
   * @param suggestionId The unique identifier of the suggestion.
   * @return A `Suggestion` object if found, `null` otherwise. The method logs an error and returns
   *   `null` if the suggestion is not found or if an error occurs during the Firestore query.
   */
  open suspend fun getSuggestionFromTrip(tripId: String, suggestionId: String): Suggestion? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
                  .document(suggestionId)
                  .get()
                  .await()
          val firestoreSuggestion = documentSnapshot.toObject<FirestoreSuggestion>()
          if (firestoreSuggestion != null) {
            firestoreSuggestion.toSuggestion()
          } else {
            Log.e(
                "TripsRepository",
                "getSuggestionFromTrip: Not found Suggestion $suggestionId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getSuggestionFromTrip: Error getting a Suggestion $suggestionId from trip $tripId.",
              e)
          null // error
        }
      }

  /**
   * Retrieves all suggestions associated with a specific trip. It iterates over all suggestion IDs
   * stored within a trip document and fetches their corresponding suggestion objects.
   *
   * @param tripId The unique identifier of the trip.
   * @return A list of `Suggestion` objects. Returns an empty list if the trip is not found, if
   *   there are no suggestions associated with the trip, or in case of an error during data
   *   retrieval.
   */
  open suspend fun getAllSuggestionsFromTrip(tripId: String): List<Suggestion> =
      withContext(dispatcher) {
        try {
          val trip = getTrip(tripId)

          if (trip != null) {
            val suggestionIds = trip.suggestions
            suggestionIds.mapNotNull { suggestionId -> getSuggestionFromTrip(tripId, suggestionId) }
          } else {
            Log.e("TripsRepository", "getAllSuggestionsFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e(
              "TripsRepository",
              "getAllSuggestionsFromTrip: Error fetching Suggestion to trip $tripId.",
              e)
          emptyList()
        }
      }

  /**
   * Adds a new suggestion to a specified trip. This involves creating a unique identifier for the
   * suggestion, converting the suggestion to a Firestore-compatible format, and updating the trip's
   * document to include the new suggestion. If successful, the method also updates the trip
   * document to include the newly added suggestion's ID in the list of suggestions.
   *
   * @param tripId The unique identifier of the trip to which the suggestion is being added.
   * @param suggestion The `Suggestion` object to be added to the trip.
   * @return `true` if the suggestion was added successfully, `false` otherwise. Errors during the
   *   process are logged.
   */
  open suspend fun addSuggestionToTrip(tripId: String, suggestion: Suggestion): Boolean =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString()
          val firebaseSuggestion =
              FirestoreSuggestion.fromSuggestion(
                  suggestion.copy(
                      suggestionId = uniqueID,
                      userId = uid)) // we already know who creates the suggestion
          val suggestionDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
                  .document(uniqueID)
          suggestionDocument.set(firebaseSuggestion).await()
          Log.d(
              "TripsRepository",
              "addSuggestionToTrip: Suggestion added successfully to trip $tripId.")

          val trip = getTrip(tripId)
          if (trip != null) {
            // Add the new suggestionId to the trip's suggestions list and update the trip
            val updatedSuggestionsList = trip.suggestions + uniqueID
            val updatedTrip = trip.copy(suggestions = updatedSuggestionsList)
            updateTrip(updatedTrip)
            Log.d(
                "TripsRepository", "addSuggestionToTrip: Suggestion ID added to trip successfully.")
            true
          } else {

            Log.e("TripsRepository", "addSuggestionToTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository", "addSuggestionToTrip: Error adding Suggestion to trip $tripId.", e)
          false
        }
      }

  /**
   * Removes a specific suggestion from a trip. This method deletes the suggestion document from the
   * Firestore subcollection and updates the trip document to remove the suggestion's ID from the
   * list of associated suggestions.
   *
   * @param tripId The unique identifier of the trip from which the suggestion is being removed.
   * @param suggestionId The unique identifier of the suggestion to remove.
   * @return `true` if the suggestion was successfully deleted and the trip updated, `false`
   *   otherwise. Errors during the process are logged.
   */
  open suspend fun removeSuggestionFromTrip(tripId: String, suggestionId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "removeSuggestionFromTrip: removing Suggestion $suggestionId from trip $tripId")
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
              .document(suggestionId)
              .delete()
              .await()

          val trip = getTrip(tripId)
          if (trip != null) {
            val updatedSuggestionsList = trip.suggestions.filterNot { it == suggestionId }
            val updatedTrip = trip.copy(suggestions = updatedSuggestionsList)
            updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "removeSuggestionFromTrip: Suggestion $suggestionId remove and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "removeSuggestionFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeSuggestionFromTrip: Error removing Suggestion $suggestionId from trip $tripId.",
              e)
          false
        }
      }

  /**
   * Updates an existing suggestion within a trip. This method replaces the suggestion document in
   * the Firestore subcollection with the updated suggestion details.
   *
   * It is important that the `suggestionId` within the `Suggestion` object matches the ID of the
   * suggestion being updated to ensure the correct document is replaced.
   *
   * @param tripId The unique identifier of the trip containing the suggestion.
   * @param suggestion The updated `Suggestion` object.
   * @return `true` if the suggestion was successfully updated, `false` otherwise. Errors during the
   *   update process are logged.
   */
  open suspend fun updateSuggestionInTrip(tripId: String, suggestion: Suggestion): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "updateSuggestionInTrip: Updating a Suggestion in trip $tripId")
          val firestoreSuggestion = FirestoreSuggestion.fromSuggestion(suggestion)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
              .document(firestoreSuggestion.suggestionId)
              .set(firestoreSuggestion)
              .await()
          Log.d(
              "TripsRepository",
              "updateSuggestionInTrip: Trip's Suggestion updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateSuggestionInTrip: Error updating stop with ID ${suggestion.suggestionId} in trip with ID $tripId",
              e)
          false
        }
      }

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
                  .get()
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

            var updatedTokensList = trip.tokenIds
            if (!SessionManager.getNotificationToken().isEmpty()) {
              updatedTokensList = updatedStopsList + SessionManager.getNotificationToken()
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

  open suspend fun getStopFromTrip(tripId: String, stopId: String): Stop? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.STOPS_SUBCOLLECTION.path)
                  .document(stopId)
                  .get()
                  .await()
          val firestoreStop = documentSnapshot.toObject<FirestoreStop>()
          if (firestoreStop != null) {
            firestoreStop.toStop()
          } else {
            Log.e("TripsRepository", "getStopFromTrip: Not found stop $stopId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getStopFromTrip: Error getting a stop $stopId from trip $tripId.",
              e)
          null // error
        }
      }

  open suspend fun getAllStopsFromTrip(tripId: String): List<Stop> =
      withContext(dispatcher) {
        try {
          val trip = getTrip(tripId)
          if (trip != null) {
            val stopIds = trip.stops
            stopIds.mapNotNull { stopId -> getStopFromTrip(tripId, stopId) }
          } else {
            Log.e("TripsRepository", "getAllStopsFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e("TripsRepository", "getAllStopsFromTrip: Error fetching stop to trip $tripId.", e)
          emptyList()
        }
      }

  open suspend fun addStopToTrip(tripId: String, stop: Stop): Boolean =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString() + "," + stop.stopId
          val firebaseStop = FirestoreStop.fromStop(stop.copy(stopId = uniqueID))
          val stopDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.STOPS_SUBCOLLECTION.path)
                  .document(uniqueID)
          stopDocument.set(firebaseStop).await()
          Log.d("TripsRepository", "addStopToTrip: Stop added successfully to trip $tripId.")

          val trip = getTrip(tripId)
          if (trip != null) {
            // Add the new stopId to the trip's stops list and update the trip
            val updatedStopsList = trip.stops + uniqueID
            val updatedTrip = trip.copy(stops = updatedStopsList)
            updateTrip(updatedTrip)
            Log.d("TripsRepository", "addStopToTrip: Stop ID added to trip successfully.")
            true
          } else {

            Log.e("TripsRepository", "addStopToTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "addStopToTrip: Error adding stop to trip $tripId.", e)
          false
        }
      }

  open suspend fun removeStopFromTrip(tripId: String, stopId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "deleteStopFromTrip: Deleting stop $stopId from trip $tripId")
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.STOPS_SUBCOLLECTION.path)
              .document(stopId)
              .delete()
              .await()

          val trip = getTrip(tripId)
          if (trip != null) {
            val updatedStopsList = trip.stops.filterNot { it == stopId }
            val updatedTrip = trip.copy(stops = updatedStopsList)
            updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "deleteStopFromTrip: Stop $stopId deleted and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "deleteStopFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "deleteStopFromTrip: Error deleting stop $stopId from trip $tripId.",
              e)
          false
        }
      }

  open suspend fun updateStopInTrip(tripId: String, stop: Stop): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "updateStopInTrip: Updating a stop in trip $tripId")
          val firestoreStop = FirestoreStop.fromStop(stop)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.STOPS_SUBCOLLECTION.path)
              .document(firestoreStop.stopId)
              .set(firestoreStop)
              .await()
          Log.d(
              "TripsRepository",
              "updateStopInTrip: Trip's Stop updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateStopInTrip: Error updating stop with ID ${stop.stopId} in trip with ID $tripId",
              e)
          false
        }
      }

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
          val documentSnapshot = tripsCollection.document(tripId).get().await()
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
          removeTripId(tripId) // remove the trip from the user
          tripsCollection.document(tripId).delete().await() // delete a given trip by its tripId
          Log.d("TripsRepository", "deleteTrip: Trip deleted successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e("TripsRepository", "deleteTrip: Error deleting trip with ID $tripId", e)
          false // Handle error or log exception
        }
      }

  /**
   * Retrieves a list of trip IDs associated with the current user. This method queries the user's
   * document in the 'Users' collection by the user's unique identifier (UID) to fetch the list of
   * trip IDs they are associated with.
   *
   * @return A list containing the trip IDs or an empty list if either the user's document does not
   *   exist or it doesn't contain any trip IDs.
   */
  open suspend fun getTripsIds(): List<String> =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "getTripsIds: Getting Trips linked to user")
          val document = usersCollection.document(uid).get().await()
          if (document.exists()) {
            // Attempts to cast the retrieved 'tripIds' field to a List<String>.
            // If 'tripIds' does not exist or is not a list, returns an empty list.
            val tripIds: MutableList<String> = mutableListOf()

            val rawTripIds = document["tripIds"]
            if (rawTripIds is List<*>) {
              tripIds.addAll(rawTripIds.filterIsInstance<String>())
            }
            // Mutable list is a subclass of List, and will so
            // tripIds will be automatically cast to List<String> when returned
            Log.d("TripsRepository", "getTripsIds: Successfully retrieved trip IDs for user $uid.")
            tripIds
          } else {
            Log.d(
                "TripsRepository",
                "getTripsIds: Failed to retrieved trip IDs for user $uid. (No document found)")
            emptyList() // In the case that the collection doesn't exist, return an empty list.
            // list.
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "getTripsIds: Error retrieving trip IDs for user $uid", e)
          emptyList()
        }
      }

  /**
   * Checks if the specified trip ID exists in the 'Trips' collection. This method queries the
   * Firestore database to verify the existence of a document corresponding to the given trip ID. It
   * ensures that operations related to trip IDs are conducted with valid identifiers.
   *
   * @param tripId The unique identifier of the trip to be validated.
   * @return Boolean indicating whether the trip ID is valid (true) or not (false). If the trip ID
   *   exists in the database, returns true; otherwise, returns false. In the event of an exception
   *   during the database query, the method also returns false,
   */
  private suspend fun isTripIdValid(tripId: String): Boolean =
      withContext(dispatcher) {
        try {
          val document = tripsCollection.document(tripId).get().await()
          if (document.exists()) {
            Log.d("TripsRepository", "isTripIdValid: tripId $tripId exists.")
            true
          } else {
            Log.d("TripsRepository", "isTripIdValid: tripId $tripId doesn't exist.")
            false
          }
        } catch (e: Exception) {
          Log.e("TripsRepository", "isTripIdValid: Error retrieving trip ID $tripId", e)
          false
        }
      }

  /**
   * Assigns a role to a user in a trip based on their ownership status and updates the trip's
   * participant list.
   *
   * This open suspend function should be invoked within a coroutine context. It fetches the current
   * user's details, assigns either the 'OWNER' or 'MEMBER' role based on the `isOwner` flag, and
   * updates the trip accordingly.
   *
   * @param tripId The unique identifier of the trip.
   * @param isOwner Indicates if the user should be added as an owner (`true`) or member (`false`).
   * @throws IllegalStateException if the current user cannot be retrieved.
   */
  private suspend fun manageUserTripRole(tripId: String, isOwner: Boolean) {
    val currentUser = SessionManager.getCurrentUser()!!
    val role = if (isOwner) Role.OWNER else Role.MEMBER
    val user =
        User(
            userId = uid,
            name = currentUser.name,
            email = currentUser.email,
            nickname = currentUser.name,
            role = role,
            notificationTokenId = SessionManager.getNotificationToken())
    addUserToTrip(tripId, user)
  }

  /**
   * Adds a trip ID to the current user's list of trip IDs in their document within the 'Users'
   * collection. If the user's document does not already contain a list of trip IDs, or if the
   * specified trip ID is not already in the list, it adds the trip ID to the list.
   *
   * This operation is performed within a Firestore transaction to ensure atomicity and consistency.
   *
   * @param tripId The unique identifier of the trip to add to the user's list of trip IDs.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  open suspend fun addTripId(tripId: String, isOwner: Boolean = false): Boolean =
      withContext(dispatcher) {
        Log.d("TripsRepository", "addTripId: Adding tripId to user")

        if (!isTripIdValid(tripId)) {
          Log.d("TripsRepository", "addTripId: isTripIdValid returned false")
          return@withContext false
        }

        val userDocumentRef = usersCollection.document(uid)

        // Ensure the user's document exists before attempting to modify it.
        // If the document does not exist, create it with an initial empty list of tripIds.
        val userDoc = userDocumentRef.get().await()
        if (!userDoc.exists()) {
          // Initialize the document with an empty tripIds list.
          userDocumentRef.set(mapOf("tripIds" to listOf<String>())).await()
        }

        try {
          val transactionResult =
              firestore
                  .runTransaction { transaction ->
                    val snapshot = transaction.get(userDocumentRef)

                    // Safely attempt to retrieve and cast the tripIds list from the snapshot.
                    val existingTripIds: MutableList<String> = mutableListOf()

                    val rawTripIds = snapshot["tripIds"]
                    if (rawTripIds is List<*>) {
                      // Filter non-null and String values only, safely adding them to
                      // existingTripIds.
                      existingTripIds.addAll(rawTripIds.filterIsInstance<String>())
                    }

                    if (!existingTripIds.contains(tripId)) {
                      existingTripIds.add(tripId)
                      transaction.update(userDocumentRef, "tripIds", existingTripIds)
                      Log.d(
                          "TripsRepository",
                          "addTripId: Successfully added trip ID $tripId to user $uid's document.")
                      true // Indicate success
                    } else {
                      Log.d(
                          "TripsRepository",
                          "addTripId: Failed trip ID $tripId with user $uid's already exist.")
                      false
                    } // No change needed
                  }
                  .await()
          if (transactionResult) {
            manageUserTripRole(tripId, isOwner)
            Log.d(
                "TripsRepository",
                "addTripId: Successfully added trip ID $tripId to user $uid's document, and updated users role.")
          }
          transactionResult
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "addTripId: Error adding trip ID $tripId to user $uid's document",
              e)
          false // On error
        }
      }

  /**
   * Remove a trip ID from a user's list of trip IDs in their document within the 'Users'
   * collection. This operation is performed within a Firestore transaction to ensure atomicity and
   * consistency. The user ID can be specified; if not, the current user's ID is used by default.
   *
   * @param tripId The unique identifier of the trip to remove from the user's list of trip IDs.
   * @param userId Optional; the unique identifier of the user from whose document the trip ID will
   *   be removed. If not provided, the ID of the current user is used as the default.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  open suspend fun removeTripId(tripId: String, userId: String = uid): Boolean =
      withContext(dispatcher) {
        Log.d("TripsRepository", "removeTripId: Removing tripId from user")

        val userDocumentRef = usersCollection.document(userId)

        try {
          val transactionResult =
              firestore
                  .runTransaction { transaction ->
                    val snapshot = transaction.get(userDocumentRef)

                    // Safely attempt to retrieve and cast the tripIds list from the snapshot.
                    val existingTripIds: MutableList<String> = mutableListOf()

                    val rawTripIds = snapshot["tripIds"]
                    if (rawTripIds is List<*>) {
                      // Filter non-null and String values only, safely adding them to
                      // existingTripIds.
                      existingTripIds.addAll(rawTripIds.filterIsInstance<String>())
                    }

                    if (existingTripIds.contains(tripId)) {
                      existingTripIds.remove(tripId)
                      transaction.update(userDocumentRef, "tripIds", existingTripIds)
                      Log.d(
                          "TripsRepository",
                          "removeTripId: Successfully removed trip ID $tripId from user $uid's document.")

                      true // Indicate success
                    } else {
                      Log.d(
                          "TripsRepository",
                          "removeTripId: Failed to removed trip ID $tripId from user $uid's document. (it didn't exist)")
                      false
                    } // No change needed
                  }
                  .await()
          transactionResult
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeTripId: Error removing trip ID $tripId from user $uid's document",
              e)
          false // On error
        }
      }
}
