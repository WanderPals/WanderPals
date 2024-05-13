package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class DocumentPSViewModel(val tripsRepository: TripsRepository, val tripId: String) :
    ViewModel() {
  open var documentslistURL = MutableStateFlow(emptyList<String>())
  open var documentslistUserURL = MutableStateFlow(emptyList<String>())

  // get all the documents from the trip
  open fun getAllDocumentsFromTrip() {
    viewModelScope.launch {
      documentslistURL.value = tripsRepository.getAllDocumentsFromTrip(tripId)
    }
  }

  // add a document to the trip
  open fun addDocumentToTrip(documentURL: String, tripID: String) {
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
      documentslistURL.value =
          tripsRepository.getAllDocumentsFromUser(SessionManager.getCurrentUser()?.userId!!, tripId)
    }
  }

  // update the documents of the current user
  open fun updateDocumentsOfCurrentUser(documentURL: String) {
    viewModelScope.launch {
      documentslistUserURL.value += documentURL
      val user = tripsRepository.getUserFromTrip(tripId, SessionManager.getCurrentUser()?.userId!!)
      if (user != null) {
        tripsRepository.updateUserInTrip(
            tripId, user.copy(documentsURL = documentslistUserURL.value))
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
