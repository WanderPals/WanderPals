package com.github.se.wanderpals.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


open class SuggestionsViewModel(private val suggestionRepository: TripsRepository?, tripId: String) :
  ViewModel() {
  // State flow to hold the list of suggestions
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  open val state: StateFlow<List<Suggestion>> = _state

  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  // the like status of each suggestion to be held to prevent repeated network calls for the same item:
  private val _likedSuggestions = MutableStateFlow<Set<String>>(emptySet())
  val likedSuggestions: StateFlow<Set<String>> = _likedSuggestions.asStateFlow()

  init {
    // Fetch all trips when the ViewModel is initialized
    loadSuggestion(tripId)
  }

  /** Fetches all trips from the repository and updates the state flow accordingly. */
  open fun loadSuggestion(tripId: String) {
    viewModelScope.launch {
      _isLoading.value = true
      // Fetch all trips from the repository
      _state.value = suggestionRepository?.getAllSuggestionsFromTrip(tripId)!!
      _isLoading.value = false
    }
  }

  /** Toggles the like status of a suggestion and updates the backend and local state accordingly.
   *
   * Note: open keyword is used to allow overriding this function in a subclass of SuggestionsViewModel,
   * namely the MockSuggestionsViewModel class when testing.
   * */
  open fun toggleLikeSuggestion(tripId: String, suggestion: Suggestion) {
    val currentLoggedInUId = suggestionRepository?.uid!! // Get the current logged-in user's ID from the repository instance
    val currentlyLiked = _likedSuggestions.value

    // Check if the suggestion is already liked by the user
    val isLiked = currentlyLiked.contains(suggestion.suggestionId) // isLiked means the suggestion is already liked

    // Toggle the like status in the local state
    _likedSuggestions.value = if (isLiked) {
      currentlyLiked - suggestion.suggestionId
    } else {
      currentlyLiked + suggestion.suggestionId
    }


    // Prepare the updated suggestion for backend update
    val updatedSuggestion = suggestion.copy(
      userLikes = if (isLiked) { // if the suggestion is already liked, remove the current user's ID
        suggestion.userLikes - currentLoggedInUId // Remove the current user's ID from the list
      } else {
        suggestion.userLikes + currentLoggedInUId
      }
    )
    // todo: print the currentLoggedInUId, suggestion.userLikes and the updatedSuggestion to see the changes and if the logic is correct

    // Update the backend by calling the TripsRepository function
    viewModelScope.launch {
      // Call the repository function to update the suggestion
      val wasUpdateSuccessful =
        suggestionRepository.updateSuggestionInTrip(tripId, updatedSuggestion)
      if (wasUpdateSuccessful) { // If the backend update is successful,
        // Update the local state with the modified suggestion
        _state.value = _state.value.map { if (it.suggestionId == suggestion.suggestionId) updatedSuggestion else it }

        // Update the list of suggestions with the new like count
        _state.value = _state.value.map { existingSuggestion ->
          if (existingSuggestion.suggestionId == suggestion.suggestionId) {
            existingSuggestion.copy(userLikes = updatedSuggestion.userLikes)
          } else {
            existingSuggestion
          }
        }
        //todo: print the _state.value, existingSuggestion and updatedSuggestion to see the changes and if the logic is correct

      } else { // If the backend update fails, revert the local state change
        _likedSuggestions.value = currentlyLiked
      }
    }
  }
}
