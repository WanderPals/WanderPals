package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.launch

open class CreateSuggestionViewModel(tripsRepository: TripsRepository) : ViewModel() {

  private val _tripsRepository = tripsRepository

  open fun addSuggestion(tripId: String, suggestion: Suggestion): Boolean {
    var a: Boolean = true
    viewModelScope.launch {
      _tripsRepository.addSuggestionToTrip(tripId, suggestion).also { a = it }
    }
    return a
  }

  class CreateSuggestionViewModelFactory(
    private val tripsRepository: TripsRepository
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(CreateSuggestionViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST")
        return CreateSuggestionViewModel(tripsRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
