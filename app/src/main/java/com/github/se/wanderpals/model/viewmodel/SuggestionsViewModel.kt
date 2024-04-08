package com.github.se.wanderpals.model.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.wanderpals.model.data.Suggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// todo: William has created this file, use his code

open class SuggestionsViewModel(/*private val suggestionRepository: TripsRepository*/ ) :
    ViewModel() {
  // State flow to hold the list of suggestions
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  open val state: StateFlow<List<Suggestion>> = _state

  //    private val _suggestionRepository = suggestionRepository

  private val _suggestions = MutableLiveData<List<Suggestion>>()
  val suggestions: LiveData<List<Suggestion>> = _suggestions

  // To be used with observeAsState
  private val _suggestionState = mutableStateOf<List<Suggestion>>(emptyList())
  val suggestionState: State<List<Suggestion>> = _suggestionState

  //    init {
  //        loadSuggestionsForTrip("tripId") // todo: Replace with actual trip ID or pass it as a
  // parameter
  //    }

  //    // Public method to load suggestions of a trip
  //    fun loadSuggestions(tripId: String) {
  //        viewModelScope.launch {
  //            try {
  //                val suggestionList = _suggestionRepository.getAllSuggestionsFromTrip(tripId)
  //                _suggestions.value = suggestionList
  //                _suggestionState.value = suggestionList
  //            } catch (e: Exception) {
  //                Log.e("SuggestionsViewModel", "Error loading suggestions", e)
  //            }
  //        }
  //    }
  //
  //    // Private method remains the same, but now it's more flexible since it's called with a trip
  // ID
  //    private fun loadSuggestionsForTrip(tripId: String) {
  //        viewModelScope.launch {
  //            try {
  //                // This will be your call to the repository or use case that fetches
  // suggestions.
  //                val suggestionList = _suggestionRepository.getAllSuggestionsFromTrip(tripId)
  //                _suggestions.value = suggestionList
  //                _suggestionState.value = suggestionList
  //            } catch (e: Exception) {
  //                // Handle any exceptions here: todo: see `suspend fun addTrip(trip: Trip)` as an
  // example
  //                Log.e("SuggestionsViewModel", "Error loading suggestions", e)
  //            }
  //        }
  //    }

}

// fixme: java.lang.RuntimeException: Cannot create an instance of class
// com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
