package com.github.se.wanderpals.model.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

open class UserRepository(
    private val firestore: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) {

    // Reference to the 'Trips' collection in Firestore
    private lateinit var tripsCollection: CollectionReference

    // Reference to the 'Users' collection in Firestore
    private lateinit var usersCollection: CollectionReference

    private lateinit var usernameCollection: CollectionReference

    fun init() {
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
    open suspend fun getUserEmail(username: String,source: Source): String? =
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
    open suspend fun addEmailToUsername(username: String, email: String,source: Source): Boolean =
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
    open suspend fun deleteEmailByUsername(username: String,source: Source): Boolean =
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
}
