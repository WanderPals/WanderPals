package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.service.SessionManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TripIDsRepository(
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) : ITripIDsRepository {
  private lateinit var firestore: FirebaseFirestore

  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  // Reference to the 'Users' collection in Firestore
  private lateinit var usersCollection: CollectionReference

  fun init(firestore: FirebaseFirestore) {
    this.firestore = firestore
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
    usersCollection = firestore.collection(FirebaseCollections.USERS_TO_TRIPS_IDS.path)
  }

  /**
   * Retrieves a list of trip IDs associated with the current user. This method queries the user's
   * document in the 'Users' collection by the user's unique identifier (UID) to fetch the list of
   * trip IDs they are associated with.
   *
   * @return A list containing the trip IDs or an empty list if either the user's document does not
   *   exist or it doesn't contain any trip IDs.
   */
  override suspend fun getTripsIds(source: Source): List<String> =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "getTripsIds: Getting Trips linked to user")
          val document = usersCollection.document(uid).get(source).await()
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
  private suspend fun isTripIdValid(tripId: String, source: Source): Boolean =
      withContext(dispatcher) {
        try {
          val document = tripsCollection.document(tripId).get(source).await()
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
            nickname = currentUser.nickname,
            role = role,
            lastPosition = currentUser.geoCords,
            profilePictureURL = currentUser.profilePhoto,
            notificationTokenId = SessionManager.getNotificationToken())
    tripsRepository.addUserToTrip(tripId, user)
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
  override suspend fun addTripId(tripId: String, isOwner: Boolean, source: Source): Boolean =
      withContext(dispatcher) {
        Log.d("TripsRepository", "addTripId: Adding tripId to user")

        if (!isTripIdValid(tripId, source)) {
          Log.d("TripsRepository", "addTripId: isTripIdValid returned false")
          return@withContext false
        }

        val userDocumentRef = usersCollection.document(uid)

        // Ensure the user's document exists before attempting to modify it.
        // If the document does not exist, create it with an initial empty list of tripIds.
        val userDoc = userDocumentRef.get(source).await()
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
  override suspend fun removeTripId(tripId: String, userId: String): Boolean =
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

                      // Indicate success
                    } else {
                      Log.d(
                          "TripsRepository",
                          "removeTripId: trip ID $tripId from user $uid's document doesn't exist.")
                    } // No change needed
                    true
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
