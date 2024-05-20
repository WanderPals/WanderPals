package com.github.se.wanderpals.model.repository

import FirestoreTrip
import android.util.Log
import com.github.se.wanderpals.model.data.Trip
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

class TripRepository(
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) {

  private lateinit var firestore: FirebaseFirestore

  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  fun init(firestore: FirebaseFirestore) {
    this.firestore = firestore
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
  }

  /**
   * Asynchronously retrieves a trip by its ID from Firestore and converts it to the data model.
   *
   * @param tripId The unique identifier of the trip to retrieve.
   * @return The Trip object if found, null otherwise.
   */
  open suspend fun getTrip(tripId: String, source: Source): Trip? =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "getTrip: Retrieving trip with ID $tripId.")
          val documentSnapshot = tripsCollection.document(tripId).get(source).await()
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
  open suspend fun getAllTrips(source: Source): List<Trip> =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "getAllTrips: Fetching all trips for user $uid.")
          val tripIds: List<String> = tripsRepository.getTripsIds()
          tripIds.mapNotNull { tripId ->
            getTrip(tripId, source) // Utilizes the getTrip method to fetch each trip individually
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
          tripsRepository.addTripId(
              uniqueID, isOwner = true) // The creator of the Trip is automatic
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

          val trip = tripsRepository.getTrip(tripId)
          if (trip == null) {
            Log.d("TripsRepository", "deleteTrip: No trip found with ID $tripId")
            return@withContext false
          }

          coroutineScope {
            // Concurrently delete all components of the trip

            launch { tripsRepository.setNotificationList(tripId, emptyList()) }
            trip.stops.forEach { stopId ->
              launch { tripsRepository.removeStopFromTrip(tripId, stopId) }
            }
            trip.suggestions.forEach { suggestionId ->
              launch { tripsRepository.removeSuggestionFromTrip(tripId, suggestionId) }
            }
            trip.announcements.forEach { announcementId ->
              launch { tripsRepository.removeAnnouncementFromTrip(tripId, announcementId) }
            }
            trip.expenses.forEach { expenseId ->
              launch { tripsRepository.removeExpenseFromTrip(tripId, expenseId) }
            }
            launch { tripsRepository.setBalances(tripId, emptyMap()) }
            launch { tripsRepository.removeTripId(tripId) }
            trip.users.forEach { userId ->
              launch { tripsRepository.removeUserFromTrip(tripId, userId) }
            }

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
}
