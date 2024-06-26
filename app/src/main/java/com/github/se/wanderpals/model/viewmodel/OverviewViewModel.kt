package com.github.se.wanderpals.model.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.service.SessionUser
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel class responsible for managing data and business logic related to the Overview screen.
 * Provides functionality to fetch all trips and exposes the trips data to the UI.
 *
 * @param tripsRepository The repository for accessing trip data.
 */
open class OverviewViewModel(private val tripsRepository: TripsRepository) : ViewModel() {

  // State flow to hold the list of trips
  private val _state = MutableStateFlow(emptyList<Trip>())
  open val state: StateFlow<List<Trip>> = _state

  // State flow to track loading state
  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  // State flow to track if the user can send a trip
  private val _canSend = MutableStateFlow(false)
  open val canSend: StateFlow<Boolean> = _canSend.asStateFlow()

  // State flow to track the user that we want to send the trip
  private val _userToSend = MutableStateFlow("")
  open val userToSend: StateFlow<String> = _userToSend.asStateFlow()

  // State flow to hold the current user
  private var _currentUser = MutableStateFlow(SessionManager.getCurrentUser())
  open val currentUser: StateFlow<SessionUser?> = _currentUser.asStateFlow()

  // signal that the trip was added successfully
  private val _createTripFinished = MutableStateFlow(false)
  open val createTripFinished: StateFlow<Boolean> = _createTripFinished.asStateFlow()

  // don't add a trip twice
  private val _isAddingTrip = MutableStateFlow(false)
  open val isAddingTrip: StateFlow<Boolean> = _isAddingTrip.asStateFlow()

  // images URL
  open val imagesURL = MutableStateFlow("")

  /** Fetches all trips from the repository and updates the state flow accordingly. */
  open fun getAllTrips() {
    viewModelScope.launch {
      // Set loading state to true before fetching data
      _isLoading.value = true
      // Fetch all trips from the repository
      _state.value = tripsRepository.getAllTrips()
      // Set loading state to false after data is fetched
      _isLoading.value = false
      // Update the current user
      _currentUser.value = SessionManager.getCurrentUser()
    }
  }
  /**
   * Adds the trip for the user by adding it in it trips Repository
   *
   * @param trip The trip to add in the repository.
   */
  open fun createTrip(trip: Trip) {
    viewModelScope.launch {
      if (!isAddingTrip.value) {

        _isAddingTrip.value = true
        tripsRepository.addTrip(trip)
        val newTripId = tripsRepository.getAllTrips().last().tripId
        NotificationsManager.addJoinTripNotification(newTripId)

        _isAddingTrip.value = false
        _createTripFinished.value = true // Signal that operation is finished
      }
    }
  }

  /** Resets the CreateTripFinished flag */
  fun setCreateTripFinished(value: Boolean) {
    _createTripFinished.value = value
  }

  /**
   * Adds the user to a trip with the specified trip ID. The trip must already exist in the database
   * to make this function success.
   *
   * @param tripId The ID of the trip to join.
   * @return True if the user successfully joins the trip, false otherwise.
   */
  open fun joinTrip(tripId: String): Boolean {
    var success = false
    var newListOfTokens = emptyList<String>()
    runBlocking {
      success = tripsRepository.addTripId(tripId)
      if (success) {
        // update the state of the user by adding the new trip in its list of trips
        val newState = _state.value.toMutableList()
        val newTrip = tripsRepository.getTrip(tripId)!!

        Log.d("JoinTrip", "Tokens: ${SessionManager.getListOfTokensTrip()}")
        Log.d("JoinTripExistingToken", "Tokens: ${newTrip.tokenIds}")

        // check if SessionManager.getNotificationToken() is already in the list of tokens

        newListOfTokens =
            if (newTrip.tokenIds.contains(SessionManager.getNotificationToken())) {
              newTrip.tokenIds
            } else {
              newTrip.tokenIds + SessionManager.getNotificationToken()
            }

        NotificationAPI()
            .sendNotification(
                newListOfTokens,
                "${SessionManager.getCurrentUser()?.name} has been added to ${newTrip.title}")

        newState.add(newTrip)
        _state.value = newState.toList()
        NotificationsManager.addJoinTripNotification(tripId)
      }
    }
    return success
  }

  /**
   * Add a user to which send an email
   *
   * @param user The user to send the trip.
   */
  open fun addUserToSend(user: String) {
    runBlocking {
      val it = tripsRepository.getUserEmail(user)
      if (it != null) {
        _userToSend.value = it
        _canSend.value = true
      }
    }
  }

  /** Clear the user to send the trip */
  open fun clearUserToSend() {
    _userToSend.value = ""
    _canSend.value = false
  }

  private fun updateTripImages(tripID: String, documentURL: String) {
    viewModelScope.launch {
      val trip = tripsRepository.getTrip(tripID)
      imagesURL.value += documentURL
      if (trip != null) {
        tripsRepository.updateTrip(trip.copy(imageUrl = imagesURL.value))
      }
    }
  }

  // Method to add the document to the current user
  open fun addDocument(
      tripId: String,
      documentsURL: Uri,
      path: String,
      context: Context,
      storageReference: StorageReference?
  ) {
    // create a reference to the uri of the image
    val riversRef = storageReference?.child("documents/${path}/${documentsURL.lastPathSegment}")
    // upload the image to the firebase storage
    val taskUp = riversRef?.putFile(documentsURL)

    // Register observers to listen for state changes
    // and progress of the upload
    taskUp
        ?.addOnFailureListener {
          // Handle unsuccessful uploads
          Log.d("Admin", "Failed to upload image")
        }
        ?.addOnSuccessListener {
          // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
          Log.d("Document", "Image uploaded successfully")
          Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
        }
    // Continue with the task to get the download URL
    taskUp
        ?.continueWithTask { task ->
          if (!task.isSuccessful) {
            task.exception?.let { throw it }
          }
          riversRef.downloadUrl
        }
        ?.addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Log.d("Admin", "Image URL: ${task.result}")
            viewModelScope.launch { updateTripImages(tripId, task.result.toString()) }
          }
        }
  }

  class OverviewViewModelFactory(private val tripsRepository: TripsRepository) :
      ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return OverviewViewModel(tripsRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
