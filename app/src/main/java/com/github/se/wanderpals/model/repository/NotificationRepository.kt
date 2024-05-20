package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.firestoreData.FirestoreTripNotification
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class NotificationRepository(
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) {

  private lateinit var firestore: FirebaseFirestore

  private val notificationsId = "Notifications"

  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  fun init(firestore: FirebaseFirestore) {
    this.firestore = firestore
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
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
  open suspend fun getNotificationList(tripId: String, source: Source): List<TripNotification> =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.TRIP_NOTIFICATIONS_SUBCOLLECTION.path)
                  .document(notificationsId)
                  .get(source)
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
      notifications: List<TripNotification>,
      source: Source
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
}
