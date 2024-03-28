package com.github.se.wanderpals.model.repository

import FirestoreTrip
import android.util.Log
import com.github.se.wanderpals.model.data.Trip
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Provides an interface for accessing and manipulating trip data stored in Firestore. Utilizes
 * Firestore's async APIs within Kotlin coroutines for non-blocking IO operations. This repository
 * abstracts away Firestore-specific details, offering a clean data model interface to the rest of
 * the application.
 *
 * @param UID Unique identifier of the current user. Used to fetch user-specific trip data.
 */
class TripsRepository(private val UID: String) {

  private lateinit var firestore: FirebaseFirestore
  // Reference to the 'Users' collection in Firestore
  private lateinit var usersCollection: CollectionReference
  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  fun initFirestore(app: FirebaseApp) {
    firestore = FirebaseFirestore.getInstance(app)
    usersCollection = firestore.collection("Users")
    tripsCollection = firestore.collection("Trips")
  }

  fun initFirestore() {
    firestore = FirebaseFirestore.getInstance()
    usersCollection = firestore.collection("Users")
    tripsCollection = firestore.collection("Trips")
  }


  /**
   * Asynchronously retrieves a trip by its ID from Firestore and converts it to the data model.
   *
   * @param tripId The unique identifier of the trip to retrieve.
   * @return The Trip object if found, null otherwise.
   */
  suspend fun getTrip(tripId: String): Trip? =
      withContext(Dispatchers.IO) {
        try {
          val documentSnapshot = tripsCollection.document(tripId).get().await()
          val firestoreTrip =
              documentSnapshot.toObject<
                  FirestoreTrip>() // Converts Firestore document to FirestoreTrip DTO
          firestoreTrip?.toTrip() // Converts FirestoreTrip DTO to Trip domain model
        } catch (e: Exception) {
          null // error
        }
      }

  /**
   * Fetches multiple trips by their IDs, leveraging the getTrip function for each ID.
   *
   * @param tripIds List of trip IDs to fetch.
   * @return List of Trip objects.
   */
  suspend fun getAllTrips(tripIds: List<String>): List<Trip> =
      withContext(Dispatchers.IO) {
        tripIds.mapNotNull { tripId ->
          try {
            getTrip(tripId) // Utilizes the getTrip method to fetch each trip individually
          } catch (e: Exception) {
            null // error
          }
        }
      }

  /**
   * Adds a new trip to Firestore, converting the Trip data model to FirestoreTrip DTO for storage.
   *
   * @param trip The Trip object to add.
   * @return Boolean indicating success or failure of the operation.
   */
  suspend fun addTrip(trip: Trip): Boolean =
      withContext(Dispatchers.IO) {
        try {
          // Generate a unique ID for the trip
          var uniqueID = UUID.randomUUID().toString()

          var existingDocument = tripsCollection.document(uniqueID).get().await()
          while (existingDocument.exists()) {
            uniqueID = UUID.randomUUID().toString()
            existingDocument = tripsCollection.document(uniqueID).get().await()
          }

          val firestoreTrip =
              FirestoreTrip.fromTrip(
                  trip.copy(tripId = uniqueID)) // Converts Trip data model to FirestoreTrip DTO
          tripsCollection
              .document(uniqueID)
              .set(firestoreTrip)
              .await() // Stores the FirestoreTrip DTO in Firestore
          addTripId(uniqueID)
          true
        } catch (e: Exception) {
          false // error
        }
      }

  /**
   * Updates an existing trip in Firestore, utilizing the same conversion process as addTrip.
   *
   * @param trip The Trip object to update.
   * @return Boolean indicating success or failure of the operation.
   */
  suspend fun updateTrip(trip: Trip): Boolean =
      withContext(Dispatchers.IO) {
        try {
          val firestoreTrip =
              FirestoreTrip.fromTrip(trip) // Converts Trip data model to FirestoreTrip DTO
          // Assuming tripId is already set in the trip object.
          tripsCollection
              .document(trip.tripId)
              .set(firestoreTrip)
              .await() // Stores the FirestoreTrip DTO in Firestore
          true
        } catch (e: Exception) {
          false // error
        }
      }

  /**
   * Deletes a trip from Firestore based on its ID.
   *
   * @param tripId The unique identifier of the trip to delete.
   * @return Boolean indicating success or failure of the operation.
   */
  suspend fun deleteTrip(tripId: String): Boolean =
      withContext(Dispatchers.IO) {
        try {
          removeTripId(tripId)
          tripsCollection.document(tripId).delete().await() // delete a given trip by its tripId

          true
        } catch (e: Exception) {
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
  suspend fun getTripsIds(): List<String> =
      withContext(Dispatchers.IO) {
        try {
          val document = usersCollection.document(UID).get().await()
          if (document.exists()) {
            // Attempts to cast the retrieved 'tripIds' field to a List<String>.
            // If 'tripIds' does not exist or is not a list, returns an empty list.
            document["tripIds"] as? List<String> ?: emptyList()
          } else {
            emptyList() // If any exception occurs during the Firestore operation, return an empty
            // list.
          }
        } catch (e: Exception) {
          emptyList()
        }
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
  suspend fun addTripId(tripId: String): Boolean =
      withContext(Dispatchers.IO) {
        val userDocumentRef = usersCollection.document(UID)

        // Ensure the user's document exists before attempting to modify it.
        // If the document does not exist, create it with an initial empty list of tripIds.
        val userDoc = userDocumentRef.get().await()
        if (!userDoc.exists()) {
          // Initialize the document with an empty tripIds list.
          userDocumentRef.set(mapOf("tripIds" to listOf<String>())).await()
        }

          try {
              val transactionResult = firestore.runTransaction { transaction ->
                  val snapshot = transaction.get(userDocumentRef)
                  val existingTripIds = snapshot["tripIds"] as? MutableList<String> ?: mutableListOf()
                  if (!existingTripIds.contains(tripId)) {
                      existingTripIds.add(tripId)
                      transaction.update(userDocumentRef, "tripIds", existingTripIds)
                      true // Indicate success
                  } else false // No change needed
              }.await()
              transactionResult
          } catch (e: Exception) {
              false // On error
          }
      }

  /**
   * remove a trip ID from the current user's list of trip IDs in their document within the 'Users'
   * collection. This operation is performed within a Firestore transaction to ensure atomicity and
   * consistency.
   *
   * @param tripId The unique identifier of the trip to add to the user's list of trip IDs.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  suspend fun removeTripId(tripId: String): Boolean =
      withContext(Dispatchers.IO) {
        val userDocumentRef = usersCollection.document(UID)

          try {
              val transactionResult = firestore.runTransaction { transaction ->
                  val snapshot = transaction.get(userDocumentRef)
                  val existingTripIds = snapshot["tripIds"] as? MutableList<String> ?: mutableListOf()
                  if (existingTripIds.contains(tripId)) {
                      existingTripIds.remove(tripId)
                      transaction.update(userDocumentRef, "tripIds", existingTripIds)
                      true // Indicate success
                  } else false // No change needed
              }.await()
              transactionResult
          } catch (e: Exception) {
              false // On error
          }

      }
}
