package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class SuggestionsViewModel(
    private val suggestionRepository: TripsRepository?,
    tripId: String
) : ViewModel() {

  private val currentLoggedInUId =
      suggestionRepository
          ?.uid!! // Get the current logged-in user's ID from the repository instance

  // State flow to hold the list of suggestions
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  open val state: StateFlow<List<Suggestion>> = _state

  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State flow to handle the displaying of the bottom sheet
    private val _bottomSheetVisible = MutableStateFlow(false)
    val bottomSheetVisible: StateFlow<Boolean> = _bottomSheetVisible.asStateFlow()

    // State flow to remember the comment that is being interacted with
    private val _selectedComment = MutableStateFlow<Comment?>(null)
    val selectedComment: StateFlow<Comment?> = _selectedComment.asStateFlow()

    // State flow to remember the suggestion that is being interacted with
    private val _selectedSuggestion = MutableStateFlow<Suggestion?>(null)
    val selectedSuggestion: StateFlow<Suggestion?> = _selectedSuggestion.asStateFlow()

  // the like status of each suggestion to be held to prevent repeated network calls for the same
  // item:
  private val _likedSuggestions = MutableStateFlow<List<String>>(emptyList())

  init {
    // Fetch all trips when the ViewModel is initialized
    loadSuggestion(tripId)
  }

  open fun getIsLiked(suggestionId: String): Boolean {
    return _likedSuggestions.value.contains(suggestionId)
  }

  open fun getNbrLiked(suggestionId: String): Int {
    return _state.value.find { it.suggestionId == suggestionId }?.userLikes?.size ?: 0
  }

  /** Fetches all trips from the repository and updates the state flow accordingly. */
  open fun loadSuggestion(tripId: String) {
    viewModelScope.launch {
      _isLoading.value = true
      // Fetch all trips from the repository
      _state.value = suggestionRepository?.getAllSuggestionsFromTrip(tripId)!!
      Log.d("Fetched Suggestions", _state.value.toString())

      _likedSuggestions.value =
          _state.value.filter { it.userLikes.contains(currentLoggedInUId) }.map { it.suggestionId }
      _isLoading.value = false
    }
  }

  /**
   * Toggles the like status of a suggestion and updates the backend and local state accordingly.
   *
   * Note: open keyword is used to allow overriding this function in a subclass of
   * SuggestionsViewModel, namely the MockSuggestionsViewModel class when testing.
   */
  open fun toggleLikeSuggestion(tripId: String, suggestion: Suggestion) {

    // Update the backend by calling the TripsRepository function
    viewModelScope.launch {
      val currentSuggestion =
          suggestionRepository?.getSuggestionFromTrip(tripId, suggestion.suggestionId)!!

      Log.d("Liked Suggestions", _likedSuggestions.value.toString())
      Log.d("Suggestions Liked Users", currentSuggestion.userLikes.toString())
      Log.d("Suggestion Is Liked", getIsLiked(currentSuggestion.suggestionId).toString())

      val liked = getIsLiked(currentSuggestion.suggestionId)

      // Toggle the like status in the local state
      _likedSuggestions.value =
          if (liked) {
            _likedSuggestions.value - currentSuggestion.suggestionId
          } else {
            _likedSuggestions.value + currentSuggestion.suggestionId
          }

      // Prepare the updated suggestion for backend update
      val newUserLike =
          if (liked) { // if the suggestion is already liked, remove the current user's ID
            currentSuggestion.userLikes -
                currentLoggedInUId // Remove the current user's ID from the list
          } else {
            currentSuggestion.userLikes + currentLoggedInUId
          }
      val updatedSuggestion = currentSuggestion.copy(userLikes = newUserLike)
      // Call the repository function to update the suggestion
      val wasUpdateSuccessful =
          suggestionRepository.updateSuggestionInTrip(tripId, updatedSuggestion)
      if (wasUpdateSuccessful) { // If the backend update is successful,
        _state.value = suggestionRepository.getAllSuggestionsFromTrip(tripId)

        _likedSuggestions.value =
            _state.value
                .filter { it.userLikes.contains(currentLoggedInUId) }
                .map { it.suggestionId }
      }
    }
  }

  open fun addComment(tripId: String, suggestion: Suggestion, comment: Comment) {
    val updatedSuggestion =
        suggestion.copy(
            comments =
                suggestion.comments +
                    Comment(
                        commentId = UUID.randomUUID().toString(),
                        userId = currentLoggedInUId,
                        userName = comment.userName,
                        text = comment.text,
                        createdAt = comment.createdAt))
    viewModelScope.launch {
      val wasUpdateSuccessful =
          suggestionRepository?.updateSuggestionInTrip(tripId, updatedSuggestion)!!
      if (wasUpdateSuccessful) {
        loadSuggestion(tripId)
      }
    }
  }

    fun setSuggestion(suggestion: Suggestion) {
        _selectedSuggestion.value = suggestion
    }

    fun setSuggestionNull() {
        _selectedSuggestion.value = null
    }

    fun showBottomSheet() {
        viewModelScope.launch {
            _bottomSheetVisible.value = true
        }
    }

    fun hideBottomSheet() {
        _bottomSheetVisible.value = false
    }

    fun deleteComment() {
        // Implement your delete logic here
        hideBottomSheet()
    }
}
