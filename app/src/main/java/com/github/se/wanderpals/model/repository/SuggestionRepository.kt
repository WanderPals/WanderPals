package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.firestoreData.FirestoreSuggestion
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SuggestionRepository(
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
   * Retrieves a specific suggestion from a trip based on the suggestion's unique identifier. This
   * method queries a subcollection within a trip document to retrieve a suggestion object based on
   * the provided `suggestionId`.
   *
   * @param tripId The unique identifier of the trip.
   * @param suggestionId The unique identifier of the suggestion.
   * @return A `Suggestion` object if found, `null` otherwise. The method logs an error and returns
   *   `null` if the suggestion is not found or if an error occurs during the Firestore query.
   */
  open suspend fun getSuggestionFromTrip(
      tripId: String,
      suggestionId: String,
      source: Source
  ): Suggestion? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
                  .document(suggestionId)
                  .get(source)
                  .await()
          val firestoreSuggestion = documentSnapshot.toObject<FirestoreSuggestion>()
          if (firestoreSuggestion != null) {
            firestoreSuggestion.toSuggestion()
          } else {
            Log.e(
                "TripsRepository",
                "getSuggestionFromTrip: Not found Suggestion $suggestionId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getSuggestionFromTrip: Error getting a Suggestion $suggestionId from trip $tripId.",
              e)
          null // error
        }
      }

  /**
   * Retrieves all suggestions associated with a specific trip. It iterates over all suggestion IDs
   * stored within a trip document and fetches their corresponding suggestion objects.
   *
   * @param tripId The unique identifier of the trip.
   * @return A list of `Suggestion` objects. Returns an empty list if the trip is not found, if
   *   there are no suggestions associated with the trip, or in case of an error during data
   *   retrieval.
   */
  open suspend fun getAllSuggestionsFromTrip(tripId: String, source: Source): List<Suggestion> =
      withContext(dispatcher) {
        try {
          val trip = tripsRepository.getTrip(tripId)

          if (trip != null) {
            val suggestionIds = trip.suggestions
            suggestionIds.mapNotNull { suggestionId ->
              getSuggestionFromTrip(tripId, suggestionId, source)
            }
          } else {
            Log.e("TripsRepository", "getAllSuggestionsFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e(
              "TripsRepository",
              "getAllSuggestionsFromTrip: Error fetching Suggestion to trip $tripId.",
              e)
          emptyList()
        }
      }

  /**
   * Adds a new suggestion to a specified trip. This involves creating a unique identifier for the
   * suggestion, converting the suggestion to a Firestore-compatible format, and updating the trip's
   * document to include the new suggestion. If successful, the method also updates the trip
   * document to include the newly added suggestion's ID in the list of suggestions.
   *
   * @param tripId The unique identifier of the trip to which the suggestion is being added.
   * @param suggestion The `Suggestion` object to be added to the trip.
   * @return `true` if the suggestion was added successfully, `false` otherwise. Errors during the
   *   process are logged.
   */
  open suspend fun addSuggestionToTrip(tripId: String, suggestion: Suggestion): Boolean =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString()
          val firebaseSuggestion =
              FirestoreSuggestion.fromSuggestion(
                  suggestion.copy(
                      suggestionId = uniqueID,
                      userId = uid)) // we already know who creates the suggestion
          val suggestionDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
                  .document(uniqueID)
          suggestionDocument.set(firebaseSuggestion).await()
          Log.d(
              "TripsRepository",
              "addSuggestionToTrip: Suggestion added successfully to trip $tripId.")

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            // Add the new suggestionId to the trip's suggestions list and update the trip
            val updatedSuggestionsList = trip.suggestions + uniqueID
            val updatedTrip = trip.copy(suggestions = updatedSuggestionsList)
            tripsRepository.updateTrip(updatedTrip)
            Log.d(
                "TripsRepository", "addSuggestionToTrip: Suggestion ID added to trip successfully.")
            true
          } else {

            Log.e("TripsRepository", "addSuggestionToTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository", "addSuggestionToTrip: Error adding Suggestion to trip $tripId.", e)
          false
        }
      }

  /**
   * Removes a specific suggestion from a trip. This method deletes the suggestion document from the
   * Firestore subcollection and updates the trip document to remove the suggestion's ID from the
   * list of associated suggestions.
   *
   * @param tripId The unique identifier of the trip from which the suggestion is being removed.
   * @param suggestionId The unique identifier of the suggestion to remove.
   * @return `true` if the suggestion was successfully deleted and the trip updated, `false`
   *   otherwise. Errors during the process are logged.
   */
  open suspend fun removeSuggestionFromTrip(tripId: String, suggestionId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "removeSuggestionFromTrip: removing Suggestion $suggestionId from trip $tripId")
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
              .document(suggestionId)
              .delete()
              .await()

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            val updatedSuggestionsList = trip.suggestions.filterNot { it == suggestionId }
            val updatedTrip = trip.copy(suggestions = updatedSuggestionsList)
            tripsRepository.updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "removeSuggestionFromTrip: Suggestion $suggestionId remove and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "removeSuggestionFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeSuggestionFromTrip: Error removing Suggestion $suggestionId from trip $tripId.",
              e)
          false
        }
      }

  /**
   * Updates an existing suggestion within a trip. This method replaces the suggestion document in
   * the Firestore subcollection with the updated suggestion details.
   *
   * It is important that the `suggestionId` within the `Suggestion` object matches the ID of the
   * suggestion being updated to ensure the correct document is replaced.
   *
   * @param tripId The unique identifier of the trip containing the suggestion.
   * @param suggestion The updated `Suggestion` object.
   * @return `true` if the suggestion was successfully updated, `false` otherwise. Errors during the
   *   update process are logged.
   */
  open suspend fun updateSuggestionInTrip(tripId: String, suggestion: Suggestion): Boolean =
      withContext(dispatcher) {
        try {
          Log.d("TripsRepository", "updateSuggestionInTrip: Updating a Suggestion in trip $tripId")
          val firestoreSuggestion = FirestoreSuggestion.fromSuggestion(suggestion)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.SUGGESTIONS_SUBCOLLECTION.path)
              .document(firestoreSuggestion.suggestionId)
              .set(firestoreSuggestion)
              .await()
          Log.d(
              "TripsRepository",
              "updateSuggestionInTrip: Trip's Suggestion updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateSuggestionInTrip: Error updating stop with ID ${suggestion.suggestionId} in trip with ID $tripId",
              e)
          false
        }
      }
}
