package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.launch

open class CreateSuggestionViewModel(tripsRepository: TripsRepository) : ViewModel() {

  private val _tripsRepository = tripsRepository

  open fun addSuggestion(tripId: String, suggestion: Suggestion): Boolean {
    var a: Boolean = false
    viewModelScope.launch {
      _tripsRepository.addSuggestionToTrip(tripId, suggestion).also { a = it }
    }
    return a
  }
}
