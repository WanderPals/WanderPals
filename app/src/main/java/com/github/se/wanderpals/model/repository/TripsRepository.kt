package com.github.se.wanderpals.model.repository

import FirestoreTrip
import android.util.Log
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.firestoreData.FirestoreStop
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
class TripsRepository(
    private val uid: String,
    private val dispatcher: CoroutineDispatcher // Inject dispatcher
) {

  private lateinit var firestore: FirebaseFirestore
  // Reference to the 'Users' collection in Firestore
  private lateinit var usersCollection: CollectionReference
  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

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
  }

    suspend fun getUserFromTrip(tripId: String,userId:String):User? = withContext(dispatcher){
        try {
            val documentSnapshot =
                tripsCollection
                    .document(tripId)
                    .collection(FirebaseCollections.STOPS_SUBCOLLECTION.path)
                    .document(userId)
                    .get()
                    .await()
            val stop = documentSnapshot.toObject<User>()
            if(stop != null){
                stop
            }else{
                Log.e("TripsRepository", "getUserFromTrip: Not found user $userId from trip $tripId.")
                null
            }
        }catch (e:Exception){
            Log.e(
                "TripsRepository",
                "getUserFromTrip: Error getting an user $userId from trip $tripId.",
                e)
            null // error
        }
    }
    suspend fun getAllUsersFromTrip(tripId: String,userId: String): List<User> = withContext(dispatcher){
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

    suspend fun addUserToTrip(tripId: String, user: User):Boolean = withContext(dispatcher){
        try {

        }catch (e:Exception){

        }
    }

    suspend fun updateUserInTrip(tripId: String, user: User):Boolean = withContext(dispatcher){
        try {

        }catch (e:Exception){

        }
    }

    suspend fun removeUserFromTrip(tripId: String,userId: String):Boolean = withContext(dispatcher){
        try {

        }catch (e:Exception){

        }
    }
  suspend fun getStopFromTrip(tripId: String, stopId: String): Stop? =
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

  suspend fun getAllStopsFromTrip(tripId: String): List<Stop> =
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

  suspend fun addStopToTrip(tripId: String, stop: Stop): Boolean =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString()
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

  suspend fun removeStopFromTrip(tripId: String, stopId: String): Boolean =
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

  suspend fun updateStopInTrip(tripId: String, stop: Stop): Boolean =
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
  suspend fun getTrip(tripId: String): Trip? =
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
  suspend fun getAllTrips(): List<Trip> =
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
  suspend fun addTrip(trip: Trip): Boolean =
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
          addTripId(uniqueID)
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
  suspend fun updateTrip(trip: Trip): Boolean =
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
  suspend fun deleteTrip(tripId: String): Boolean =
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
  suspend fun getTripsIds(): List<String> =
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
      withContext(dispatcher) {
        Log.d("TripsRepository", "addTripId: Adding tripId to user")

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
          Log.d(
              "TripsRepository",
              "addTripId: Successfully added trip ID $tripId to user $uid's document.")

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
   * remove a trip ID from the current user's list of trip IDs in their document within the 'Users'
   * collection. This operation is performed within a Firestore transaction to ensure atomicity and
   * consistency.
   *
   * @param tripId The unique identifier of the trip to add to the user's list of trip IDs.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  suspend fun removeTripId(tripId: String): Boolean =
      withContext(dispatcher) {
        Log.d("TripsRepository", "removeTripId: Removing tripId from user")

        val userDocumentRef = usersCollection.document(uid)

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
