package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class DashboardViewModel(private val tripsRepository: TripsRepository, tripId: String) :
    ViewModel() {
  // State flow to hold the list of suggestions
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  open val state: StateFlow<List<Suggestion>> = _state

  private val _expenses = MutableStateFlow(emptyList<Expense>())
  open val expenses: StateFlow<List<Expense>> = _expenses

  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private var _tripTitle = MutableStateFlow("")
  open val tripTitle: StateFlow<String> = _tripTitle.asStateFlow()

  private var _lastSharedDocument = MutableStateFlow("")
  open val lastSharedDocument: StateFlow<String> = _lastSharedDocument.asStateFlow()

  private var _lastPrivateDocument = MutableStateFlow("")
  open val lastPrivateDocument: StateFlow<String> = _lastPrivateDocument.asStateFlow()

  /** Fetches all trips from the repository and updates the state flow accordingly. */
  open fun loadSuggestion(tripId: String) {
    viewModelScope.launch {
      _isLoading.value = true
      // Fetch all suggestions from the trip
      _state.value = tripsRepository.getAllSuggestionsFromTrip(tripId)
      _isLoading.value = false
    }
  }

  open fun loadExpenses(tripId: String) {
    viewModelScope.launch {
      // Fetch all expenses from the trip
      _expenses.value = tripsRepository.getAllExpensesFromTrip(tripId)
    }
  }

  open fun loadTripTitle(tripId: String) {
    // Get the title of the trip
    runBlocking { _tripTitle.value = tripsRepository.getTrip(tripId)?.title ?: "" }
  }

  open fun loadLastAddedSharedDocument(tripId: String) {
    // Get the last added document
    viewModelScope.launch {
      val docs = tripsRepository.getTrip(tripId)?.documentsURL ?: emptyList()
      if (docs.isNotEmpty()) {
        _lastSharedDocument.value = docs.last().documentsName
      }
    }
  }

  open fun loadLastAddedPrivateDocument(tripId: String, userID: String) {
    // Get the last added document
    viewModelScope.launch {
      val privateDocs = tripsRepository.getUserFromTrip(tripId, userID)?.documentsURL ?: emptyList()
      if (privateDocs.isNotEmpty()) {
        _lastPrivateDocument.value = privateDocs.last().documentsName
      }
    }
  }

  class DashboardViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return DashboardViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
