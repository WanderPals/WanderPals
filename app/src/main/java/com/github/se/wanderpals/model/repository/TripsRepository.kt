package com.github.se.wanderpals.model.repository

import FirestoreTrip
import com.github.se.wanderpals.model.data.Trip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Provides an interface for accessing and manipulating trip data stored in Firestore.
 * Utilizes Firestore's async APIs within Kotlin coroutines for non-blocking IO operations.
 * This repository abstracts away Firestore-specific details, offering a clean data model
 * interface to the rest of the application.
 *
 * @param UID Unique identifier of the current user. Used to fetch user-specific trip data.
 */
class TripsRepository(private val UID: String) {

    private val firestore = FirebaseFirestore.getInstance()
    // Reference to the 'Users' collection in Firestore
    private val usersCollection = firestore.collection("Users")
    // Reference to the 'Trips' collection in Firestore
    private val tripsCollection = firestore.collection("Trips")

    /**
     * Asynchronously retrieves a trip by its ID from Firestore and converts it to the data model.
     * @param tripId The unique identifier of the trip to retrieve.
     * @return The Trip object if found, null otherwise.
     */
    suspend fun getTrip(tripId: String): Trip? = withContext(Dispatchers.IO){
        try {
            val documentSnapshot = tripsCollection.document(tripId).get().await()
            val firestoreTrip = documentSnapshot.toObject<FirestoreTrip>() // Converts Firestore document to FirestoreTrip DTO
            firestoreTrip?.toTrip()// Converts FirestoreTrip DTO to Trip domain model
        }catch (e: Exception){
            null //error
        }
    }

    /**
     * Fetches multiple trips by their IDs, leveraging the getTrip function for each ID.
     * @param tripIds List of trip IDs to fetch.
     * @return List of Trip objects.
     */
    suspend fun getAllTrips(tripIds: List<String>): List<Trip> = withContext(Dispatchers.IO){
        tripIds.mapNotNull { tripId ->
            try {
                getTrip(tripId) // Utilizes the getTrip method to fetch each trip individually
            } catch (e: Exception) {
                null //error
            }
        }
    }

    /**
     * Adds a new trip to Firestore, converting the Trip domain model to FirestoreTrip DTO for storage.
     * @param trip The Trip object to add.
     * @return Boolean indicating success or failure of the operation.
     */
    suspend fun addTrip(trip: Trip): Boolean = withContext(Dispatchers.IO){
        try {
            val firestoreTrip = FirestoreTrip.fromTrip(trip.copy(tripId = "")) // Converts Trip data model to FirestoreTrip DTO
            tripsCollection.document(trip.tripId).set(firestoreTrip).await() // Stores the FirestoreTrip DTO in Firestore
            true
        } catch (e: Exception) {
            false // error
        }
    }

    /**
     * Updates an existing trip in Firestore, utilizing the same conversion process as addTrip.
     * @param trip The Trip object to update.
     * @return Boolean indicating success or failure of the operation.
     */
    suspend fun updateTrip(trip: Trip): Boolean = withContext(Dispatchers.IO){
        try {
            val firestoreTrip = FirestoreTrip.fromTrip(trip) // Converts Trip data model to FirestoreTrip DTO
            // Assuming tripId is already set in the trip object.
            tripsCollection.document(trip.tripId).set(firestoreTrip).await() // Stores the FirestoreTrip DTO in Firestore
            true
        } catch (e: Exception) {
            false // error
        }
    }

    /**
     * Deletes a trip from Firestore based on its ID.
     * @param tripId The unique identifier of the trip to delete.
     * @return Boolean indicating success or failure of the operation.
     */
    suspend fun deleteTrip(tripId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            tripsCollection.document(tripId).delete().await() //delete a given trip by its tripId
            true
        } catch (e: Exception) {
            false // Handle error or log exception
        }
    }




    suspend fun getTripsIds(): List<String> = withContext(Dispatchers.IO){
        try {
            val document = usersCollection.document(UID).get().await()
            if(document.exists()){
                document["tripIds"] as? List<String> ?: emptyList()
            }else{
                emptyList()
            }

        }catch (e: Exception){
            emptyList()
        }
    }

    suspend fun addTripId(tripId: String):Boolean = withContext(Dispatchers.IO){

        val userDocumentRef = usersCollection.document(UID)

        try {
            suspendCancellableCoroutine<Boolean> { continuation ->
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userDocumentRef)
                    val existingTripIds = snapshot["tripIds"] as? MutableList<String> ?: mutableListOf()
                    if(!existingTripIds.contains(tripId)){
                        existingTripIds.add(tripId)
                        transaction.update(userDocumentRef,"tripIds",existingTripIds)
                    }
                }.addOnSuccessListener {
                    continuation.resume(true)
                }.addOnFailureListener{exception->
                    continuation.resumeWithException(exception)
                }
            }

        }catch (e: Exception){
            false //operation failed
        }
    }
}