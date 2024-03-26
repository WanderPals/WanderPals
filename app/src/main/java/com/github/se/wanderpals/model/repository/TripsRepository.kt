package com.github.se.wanderpals.model.repository

import com.github.se.wanderpals.model.data.Trip
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TripsRepository(private val UID: String) {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("Users")
    private val tripsCollection = firestore.collection("Trips")

    suspend fun getTrip(tripId: String): Trip? = withContext(Dispatchers.IO){
        try {
            val documentSnapshot = tripsCollection.document(tripId).get().await()
            documentSnapshot.toObject(Trip::class.java)
        }catch (e: Exception){
            null
        }
    }

    suspend fun getAllTrips(tripIds: List<String>): List<Trip> = withContext(Dispatchers.IO){
        tripIds.mapNotNull { tripId ->
            try {
                getTrip(tripId) // Reusing getTrip function for each ID
            } catch (e: Exception) {
                null // Handle error or log exception
            }
        }
    }

    suspend fun addTrip(trip: Trip): Boolean = withContext(Dispatchers.IO){
        try {
            // Assuming tripId is already set in the trip object.
            tripsCollection.document(trip.tripId).set(trip).await()
            true
        } catch (e: Exception) {
            false // Handle error or log exception
        }
    }

    suspend fun updateTrip(trip: Trip){

    }

    suspend fun deleteTrip(trip: Trip){

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