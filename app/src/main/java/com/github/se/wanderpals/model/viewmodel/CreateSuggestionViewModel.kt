package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.NotificationsManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class CreateSuggestionViewModel(tripsRepository: TripsRepository) : ViewModel() {

  private val _tripsRepository = tripsRepository

  open fun addSuggestion(tripId: String, suggestion: Suggestion): Boolean {
    var a: Boolean = true
    viewModelScope.launch {
      _tripsRepository.addSuggestionToTrip(tripId, suggestion).also { a = it }
      runBlocking {
        val newSuggestion = _tripsRepository.getAllSuggestionsFromTrip(tripId).last()
        NotificationsManager.addCreateSuggestionNotification(tripId, newSuggestion.suggestionId)

        /*for (userToken in SessionManager.getListOfTokensTrip()) {
          sendMessageToListOfUsers(
              userToken,
              "A new suggestion has been added to ${SessionManager.getCurrentUser()?.tripName}")
        }*/
      }
    }
    return a
  }

  open fun updateSuggestion(tripId: String, suggestion: Suggestion): Boolean {
    var success: Boolean = true
    viewModelScope.launch {
      _tripsRepository.updateSuggestionInTrip(tripId, suggestion).also { success = it }
    }
    return success
  }

  class CreateSuggestionViewModelFactory(private val tripsRepository: TripsRepository) :
      ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(CreateSuggestionViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return CreateSuggestionViewModel(tripsRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
