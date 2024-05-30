package com.github.se.wanderpals.model.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Documents
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing documents for a specific trip.
 *
 * @param tripsRepository The repository used to access trip data.
 * @param tripId The ID of the trip for which documents are managed.
 */
open class DocumentPSViewModel(val tripsRepository: TripsRepository, val tripId: String) :
    ViewModel() {
  open var documentslistURL = MutableStateFlow(emptyList<Documents>())
  open var documentslistUserURL = MutableStateFlow(emptyList<Documents>())

  // get all the documents from the trip
  open fun getAllDocumentsFromTrip() {
    viewModelScope.launch {
      val trip = tripsRepository.getTrip(tripId)
      documentslistURL.value = trip?.documentsURL ?: emptyList()
    }
  }

  // add a document to the trip
  private fun addDocumentToTrip(documentURL: Documents, tripID: String) {
    viewModelScope.launch {
      val trip = tripsRepository.getTrip(tripID)
      documentslistURL.value += documentURL
      if (trip != null) {
        tripsRepository.updateTrip(trip.copy(documentsURL = documentslistURL.value))
      }
    }
  }

  // get all the documents from the current User
  open fun getAllDocumentsFromCurrentUser() {
    viewModelScope.launch {
      val user = tripsRepository.getUserFromTrip(tripId, SessionManager.getCurrentUser()?.userId!!)
      documentslistUserURL.value = user?.documentsURL ?: emptyList()
    }
  }

  // update the documents of the current user
  private fun updateDocumentsOfCurrentUser(documentURL: Documents) {
    viewModelScope.launch {
      documentslistUserURL.value += documentURL
      val user = tripsRepository.getUserFromTrip(tripId, SessionManager.getCurrentUser()?.userId!!)
      if (user != null) {
        tripsRepository.updateUserInTrip(
            tripId, user.copy(documentsURL = documentslistUserURL.value))
      }
    }
  }

  // Method to add the document to the current user
  open fun addDocument(
      documentsName: String,
      documentsURL: Uri,
      path: String,
      context: Context,
      storageReference: StorageReference?,
      state: Int
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
          if (task.isSuccessful && state == 0) {
            viewModelScope.launch {
              updateDocumentsOfCurrentUser(Documents(task.result.toString(), documentsName))
            }
            Log.d("Admin", "Image URL: ${task.result}")
          } else if (task.isSuccessful && state == 1) {
            viewModelScope.launch {
              addDocumentToTrip(Documents(task.result.toString(), documentsName), tripId)
            }
            // empty the list
            Log.d("Admin", "Image URL: ${task.result}")
          }
        }
  }

  class DocumentPSViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(DocumentPSViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return DocumentPSViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
