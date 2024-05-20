package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.firestoreData.FirestoreStop
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class StopRepository(
    private val firestore: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) {
    // Reference to the 'Trips' collection in Firestore
    private lateinit var tripsCollection: CollectionReference

    fun init() {
        tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
    }


    open suspend fun getStopFromTrip(tripId: String, stopId: String,source: Source): Stop? =
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

    open suspend fun getAllStopsFromTrip(tripId: String,source: Source): List<Stop> =
        withContext(dispatcher) {
            try {
                val trip = tripsRepository.getTrip(tripId)
                if (trip != null) {
                    val stopIds = trip.stops
                    stopIds.mapNotNull { stopId -> getStopFromTrip(tripId, stopId,source) }
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
}
