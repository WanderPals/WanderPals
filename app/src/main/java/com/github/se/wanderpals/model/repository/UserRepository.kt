package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.firestoreData.FirestoreUser
import com.github.se.wanderpals.service.SessionManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class UserRepository(
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) {

  private lateinit var firestore: FirebaseFirestore

  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference

  // Reference to the 'Users' collection in Firestore
  private lateinit var usersCollection: CollectionReference

  private lateinit var usernameCollection: CollectionReference

  fun init(firestore: FirebaseFirestore) {
    this.firestore = firestore
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
    usersCollection = firestore.collection(FirebaseCollections.USERS_TO_TRIPS_IDS.path)
    usernameCollection = firestore.collection(FirebaseCollections.USERNAME_TO_EMAIL_COLLECTION.path)
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
  open suspend fun getUserEmail(username: String, source: Source): String? =
      withContext(dispatcher) {
        try {
          val documentSnapshot = usernameCollection.document(username).get(source).await()
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
  open suspend fun addEmailToUsername(username: String, email: String, source: Source): Boolean =
      withContext(dispatcher) {
        try {

          val documentRef = usernameCollection.document(username)
          val snapshot = documentRef.get(source).await()

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
  open suspend fun deleteEmailByUsername(username: String, source: Source): Boolean =
      withContext(dispatcher) {
        try {
          val documentRef = usernameCollection.document(username)
          val snapshot = documentRef.get(source).await()

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
   * Fetches a user's details for a specific trip. This method queries a subcollection within a trip
   * document to retrieve a user object based on the provided `userId`.
   *
   * @param tripId The unique identifier of the trip.
   * @param userId The unique identifier of the user.
   * @return A `User` object if found, `null` otherwise.
   */
  open suspend fun getUserFromTrip(tripId: String, userId: String, source: Source): User? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.USERS_SUBCOLLECTION.path)
                  .document(userId)
                  .get(source)
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
  open suspend fun getAllUsersFromTrip(tripId: String, source: Source): List<User> =
      withContext(dispatcher) {
        try {
          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            val stopIds = trip.users
            stopIds.mapNotNull { userId -> getUserFromTrip(tripId, userId, source) }
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
          val trip = tripsRepository.getTrip(tripId)
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
            tripsRepository.updateTrip(updatedTrip)
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

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            val updatedUsersList = trip.users.filterNot { it == userId }
            var updatedTokensList = trip.tokenIds
            if (trip.tokenIds.contains(SessionManager.getNotificationToken())) {
              updatedTokensList = updatedTokensList - SessionManager.getNotificationToken()
            }
            val updatedTrip = trip.copy(users = updatedUsersList, tokenIds = updatedTokensList)

            tripsRepository.updateTrip(updatedTrip)
            tripsRepository.removeTripId(
                tripId, userId) // remove the Trip from the the deleted user
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
}
