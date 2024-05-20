package com.github.se.wanderpals.model.repository

import android.util.Log
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.firestoreData.FirestoreAnnouncement
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AnnouncementRepository(
    private val dispatcher: CoroutineDispatcher,
    var uid: String,
    private val tripsRepository: TripsRepository
) : IAnnouncementRepository {

  // Reference to the 'Trips' collection in Firestore
  private lateinit var tripsCollection: CollectionReference
  private lateinit var firestore: FirebaseFirestore

  fun init(firestore: FirebaseFirestore) {
    this.firestore = firestore
    tripsCollection = firestore.collection(FirebaseCollections.TRIPS.path)
  }

  /**
   * Retrieves a specific trip Announcement from a trip based on the Announcement's unique
   * identifier. This method queries the Firestore subcollection for trip Announcements within a
   * specific trip document.
   *
   * @param tripId The unique identifier of the trip.
   * @param announcementId The unique identifier of the trip Announcement to be retrieved.
   * @return A `Announcement` object if found, or `null` if the Announcement is not found or if an
   *   error occurs. The method logs an error and returns `null` in case of failure.
   */
  override suspend fun getAnnouncementFromTrip(
      tripId: String,
      announcementId: String,
      source: Source
  ): Announcement? =
      withContext(dispatcher) {
        try {
          val documentSnapshot =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
                  .document(announcementId)
                  .get(source)
                  .await()
          val firestoreAnnouncement = documentSnapshot.toObject<FirestoreAnnouncement>()
          if (firestoreAnnouncement != null) {
            firestoreAnnouncement.toAnnouncement()
          } else {
            Log.e(
                "TripsRepository",
                "getAnnouncementFromTrip: Not found Announcement $announcementId from trip $tripId.")
            null
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "getAnnouncementFromTrip: Error getting a Announcement $announcementId from trip $tripId.",
              e)
          null // error
        }
      }

  /**
   * Retrieves all trip Announcements associated with a specific trip. It iterates over all
   * Announcement IDs stored within the trip document and fetches their corresponding trip
   * Announcement objects.
   *
   * @param tripId The unique identifier of the trip.
   * @return A list of `Announcement` objects. Returns an empty list if the trip is not found, if
   *   there are no Announcements associated with the trip, or in case of an error during data
   *   retrieval.
   */
  override suspend fun getAllAnnouncementsFromTrip(
      tripId: String,
      source: Source
  ): List<Announcement> =
      withContext(dispatcher) {
        try {
          val trip = tripsRepository.getTrip(tripId)

          if (trip != null) {
            coroutineScope { // Create a new coroutine scope to manage child jobs
              trip.announcements
                  .map { announcementId ->
                    async { // Launch a new coroutine for each announcement fetch
                      getAnnouncementFromTrip(tripId, announcementId, source)
                    }
                  }
                  .awaitAll()
                  .filterNotNull() // Wait for all fetches to complete and filter out null results
            }
          } else {
            Log.e("TripsRepository", "getAllAnnouncementsFromTrip: Trip not found with ID $tripId.")
            emptyList()
          }
        } catch (e: Exception) {

          Log.e(
              "TripsRepository",
              "getAllAnnouncementsFromTrip: Error fetching Announcement to trip $tripId.",
              e)
          emptyList()
        }
      }

  /**
   * Adds a Announcement to a specific trip in the Firestore database. This method generates a
   * unique ID for the new Announcement, creates a corresponding FirestoreAnnouncement object, and
   * commits it to the trip's Announcements subcollection.
   *
   * It also updates the trip's document to include this new Announcement ID in its list.
   *
   * @param tripId The unique identifier of the trip where the Announcement will be added.
   * @param announcement The Announcement object to add.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  override suspend fun addAnnouncementToTrip(tripId: String, announcement: Announcement): Boolean =
      withContext(dispatcher) {
        try {
          val uniqueID = UUID.randomUUID().toString()
          val firebaseAnnouncement =
              FirestoreAnnouncement.fromAnnouncement(
                  announcement.copy(
                      announcementId = uniqueID,
                      userId = uid)) // we already know who creates the Announcement
          val announcementDocument =
              tripsCollection
                  .document(tripId)
                  .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
                  .document(uniqueID)
          announcementDocument.set(firebaseAnnouncement).await()
          Log.d(
              "TripsRepository",
              "addAnnouncementToTrip: Announcement added successfully to trip $tripId.")

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            // Add the new AnnouncementId to the trip's Announcements list and update the
            // trip
            val updatedAnnouncementsList = trip.announcements + uniqueID
            val updatedTrip = trip.copy(announcements = updatedAnnouncementsList)
            tripsRepository.updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "addAnnouncementToTrip: Announcement ID added to trip successfully.")
            true
          } else {

            Log.e("TripsRepository", "addAnnouncementToTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "addAnnouncementToTrip: Error adding Announcement to trip $tripId.",
              e)
          false
        }
      }

  /**
   * Removes a specific Announcement from a trip's Announcement list and the Firestore database.
   * This method first deletes the Announcement document from the trip's Announcement subcollection.
   *
   * It also updates the trip's document to remove the Announcement ID from its list.
   *
   * @param tripId The unique identifier of the trip.
   * @param announcementId The unique identifier of the Announcement to be removed.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  override suspend fun removeAnnouncementFromTrip(tripId: String, announcementId: String): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "removeAnnouncementFromTrip: removing Announcement $announcementId from trip $tripId")
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
              .document(announcementId)
              .delete()
              .await()

          val trip = tripsRepository.getTrip(tripId)
          if (trip != null) {
            val updatedAnnouncementsList = trip.announcements.filterNot { it == announcementId }
            val updatedTrip = trip.copy(announcements = updatedAnnouncementsList)
            tripsRepository.updateTrip(updatedTrip)
            Log.d(
                "TripsRepository",
                "removeAnnouncementFromTrip: Announcement $announcementId remove and trip updated successfully.")
            true
          } else {
            Log.e("TripsRepository", "removeAnnouncementFromTrip: Trip not found with ID $tripId.")
            false
          }
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "removeAnnouncementFromTrip: Error removing Announcement $announcementId from trip $tripId.",
              e)
          false
        }
      }

  /**
   * Updates a specific Announcement in a trip's Announcement subcollection in the Firestore
   * database. This method creates a FirestoreAnnouncement object from the provided Announcement
   * object and sets it to the corresponding document identified by the Announcement ID.
   *
   * @param tripId The unique identifier of the trip.
   * @param announcement The Announcement object that contains updated data.
   * @return Boolean indicating the success (true) or failure (false) of the operation.
   */
  override suspend fun updateAnnouncementInTrip(
      tripId: String,
      announcement: Announcement
  ): Boolean =
      withContext(dispatcher) {
        try {
          Log.d(
              "TripsRepository",
              "updateAnnouncementInTrip: Updating a Announcement in trip $tripId")
          val firestoreAnnouncement = FirestoreAnnouncement.fromAnnouncement(announcement)
          tripsCollection
              .document(tripId)
              .collection(FirebaseCollections.ANNOUNCEMENTS_SUBCOLLECTION.path)
              .document(firestoreAnnouncement.announcementId)
              .set(firestoreAnnouncement)
              .await()
          Log.d(
              "TripsRepository",
              "updateAnnouncementInTrip: Trip's Announcement updated successfully for ID $tripId.")
          true
        } catch (e: Exception) {
          Log.e(
              "TripsRepository",
              "updateAnnouncementInTrip: Error updating Announcement with ID ${announcement.announcementId} in trip with ID $tripId",
              e)
          false
        }
      }
}
