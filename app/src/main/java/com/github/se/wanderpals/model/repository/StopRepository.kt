package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.firestoreData.FirestoreStop
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StopRepository(
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) : IStopRepository {

  private lateinit var firestore: FirebaseFirestore
  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  fun init(firestore: FirebaseFirestore) {
    this.firestore = firestore
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
  }

  /**
   * Retrieves a specific stop from a trip based on the stop's unique identifier. This method
   * queries a subcollection within a trip document to retrieve a stop object based on the provided
   * `stopId`.
   *
   * @param tripId The unique identifier of the trip.
   * @param stopId The unique identifier of the stop.
   * @return A `Stop` object if found, `null` otherwise. The method logs an error and returns `null`
   *   if the stop is not found or if an error occurs during the Firestore query.
   */
  override suspend fun getStopFromTrip(tripId: String, stopId: String, source: Source): Stop? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.STOPS_SUBCOLLECTION.path)
                  .document(stopId)
                  .get(source)
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

  /**
   * Retrieves all stops associated with a specific trip. It iterates over all stop IDs stored
   * within a trip document and fetches their corresponding stop objects.
   *
   * @param tripId The unique identifier of the trip.
   * @return A list of `Stop` objects. Returns an empty list if the trip is not found, if there are
   *   no stops associated with the trip, or in case of an error during data retrieval.
   */
  override suspend fun getAllStopsFromTrip(tripId: String, source: Source): List<Stop> =
      withContext(dispatcher) {
        try {
          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            val stopIds = trip.stops
            stopIds.mapNotNull { stopId -> getStopFromTrip(tripId, stopId, source) }
          } else {
            Log.e("TripsRepository", "getAllStopsFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e("TripsRepository", "getAllStopsFromTrip: Error fetching stop to trip $tripId.", e)
          emptyList()
        }
      }

  /**
   * Adds a stop to a specified trip. This involves creating a unique identifier for the stop,
   * converting the stop to a Firestore-compatible format, and updating the trip's document to
   * include the new stop. If successful, the method also updates the trip document to include the
   * newly added stop's ID in the list of stops.
   *
   * @param tripId The unique identifier of the trip to which the stop is being added.
   * @param stop The `Stop` object to be added to the trip.
   * @return `true` if the stop was added successfully, `false` otherwise. Errors during the process
   *   are logged.
   */
  override suspend fun addStopToTrip(tripId: String, stop: Stop): Boolean =
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

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            // Add the new stopId to the trip's stops list and update the trip
            val updatedStopsList = trip.stops + uniqueID
            val updatedTrip = trip.copy(stops = updatedStopsList)
            tripsRepository.updateTrip(updatedTrip)
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

  /**
   * Removes a specific stop from a trip. This method deletes the stop document from the Firestore
   * subcollection and updates the trip document to remove the stop's ID from the list of associated
   * stops.
   *
   * @param tripId The unique identifier of the trip.
   * @param stopId The unique identifier of the stop to remove.
   * @return `true` if the stop was successfully deleted and the trip updated, `false` otherwise.
   *   Errors during the process are logged.
   */
  override suspend fun removeStopFromTrip(tripId: String, stopId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "deleteStopFromTrip: Deleting stop $stopId from trip $tripId")

          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.STOPS_SUBCOLLECTION.path)
              .document(stopId)
              .delete()
              .await()

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            val updatedStopsList = trip.stops.filterNot { it == stopId }
            val updatedTrip = trip.copy(stops = updatedStopsList)
            tripsRepository.updateTrip(updatedTrip)
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

  /**
   * Updates an existing stop within a trip. This method replaces the stop document in the Firestore
   * subcollection with the updated stop details.
   *
   * It is important that the `stopId` within the `Stop` object matches the ID of the stop being
   * updated to ensure the correct document is replaced.
   *
   * @param tripId The unique identifier of the trip containing the stop.
   * @param stop The updated `Stop` object.
   * @return `true` if the stop was successfully updated, `false` otherwise. Errors during the
   *   update process are logged.
   */
  override suspend fun updateStopInTrip(tripId: String, stop: Stop): Boolean =
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
}
