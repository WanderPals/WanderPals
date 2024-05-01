package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import java.time.LocalTime
import java.util.UUID
import kotlin.math.ceil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Fetches all users from a trip and calculates the threshold for majority based on the number of
 * users.
 *
 * @param suggestionRepository The repository to fetch data from.
 * @param tripId The ID of the trip to fetch users from.
 * @return A pair of the list of users and the threshold for majority.
 */
private suspend fun fetchUsersAndThreshold(
    suggestionRepository: TripsRepository,
    tripId: String
): Pair<List<User>, Int> {
  val allUsers = suggestionRepository.getAllUsersFromTrip(tripId)
  val threshold = ceil(allUsers.size / 2.0).toInt()
  return Pair(allUsers, threshold)
}

open class SuggestionsViewModel(
    private val suggestionRepository: TripsRepository?,
    val tripId: String
) : ViewModel() {

  private val currentLoggedInUId =
      suggestionRepository
          ?.uid!! // Get the current logged-in user's ID from the repository instance

  // State flow to hold the list of suggestions
  private val _state = MutableStateFlow(emptyList<Suggestion>())
  open val state: StateFlow<List<Suggestion>> = _state

  private val _isLoading = MutableStateFlow(true)
  open val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _isLikeChanging = MutableStateFlow(false)

  // State flow to handle the displaying of the bottom sheet
  private val _bottomSheetVisible = MutableStateFlow(false)
  open val bottomSheetVisible: StateFlow<Boolean> = _bottomSheetVisible.asStateFlow()

  // State flow to remember the comment that is being interacted with
  private val _selectedComment = MutableStateFlow<Comment?>(null)
  open val selectedComment: StateFlow<Comment?> = _selectedComment.asStateFlow()

  // State flow to handle the editing of a comment
  private val _editingComment = MutableStateFlow<Boolean>(false)
  open val editingComment: StateFlow<Boolean> = _editingComment.asStateFlow()

  // State flow to remember the suggestion that is being interacted with
  private val _selectedSuggestion = MutableStateFlow<Suggestion?>(null)
  open val selectedSuggestion: StateFlow<Suggestion?> = _selectedSuggestion.asStateFlow()

  // State flow to handle the displaying of the delete dialog
  private val _showDeleteDialog = MutableStateFlow(false)
  open val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

  // the like status of each suggestion to be held to prevent repeated network calls for the same
  // item:
  private val _likedSuggestions = MutableStateFlow<List<String>>(emptyList())

  // This will hold the IDs of suggestions that have been added to stops
  private val _addedSuggestionsToStops = MutableStateFlow<List<String>>(emptyList())
  open val addedSuggestionsToStops: StateFlow<List<String>> = _addedSuggestionsToStops.asStateFlow()

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
      delay(1000)
      val suggestions = suggestionRepository?.getAllSuggestionsFromTrip(tripId)!!
      _state.value = suggestions
      Log.d("Fetched Suggestions", _state.value.toString())

      _likedSuggestions.value =
          _state.value.filter { it.userLikes.contains(currentLoggedInUId) }.map { it.suggestionId }

      // Fetch all users from the trip once, before the loop
      val (allUsers, threshold) = fetchUsersAndThreshold(suggestionRepository, tripId)

      // After loading, check if any suggestions have already reached the majority.
      // If so, add them as stops and remove them from the suggestions list. This is done
      // automatically.
      suggestions.forEach { suggestion ->
        checkAndAddSuggestionAsStop(suggestion, allUsers, threshold)
        if (selectedSuggestion.value?.suggestionId == suggestion.suggestionId) {
          _selectedSuggestion.value = suggestion
        }
      }

      _isLoading.value = false
    }
  }

  open fun setSelectedSuggestion(suggestion: Suggestion) {
    _selectedSuggestion.value = suggestion
  }

  /**
   * Toggles the like status of a suggestion and updates the backend and local state accordingly.
   *
   * Note: open keyword is used to allow overriding this function in a subclass of
   * SuggestionsViewModel, namely the MockSuggestionsViewModel class when testing.
   */
  open fun toggleLikeSuggestion(suggestion: Suggestion) {

    if (!_isLikeChanging.value) {

      _isLikeChanging.value = true
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

        // Fetch all users from the trip:
        val (allUsers, threshold) = fetchUsersAndThreshold(suggestionRepository, tripId)

        // Call the repository function to update the suggestion
        val wasUpdateSuccessful =
            suggestionRepository.updateSuggestionInTrip(tripId, updatedSuggestion)
        if (wasUpdateSuccessful) { // If the backend update is successful,
          _state.value = suggestionRepository.getAllSuggestionsFromTrip(tripId)

          _likedSuggestions.value =
              _state.value
                  .filter { it.userLikes.contains(currentLoggedInUId) }
                  .map { it.suggestionId }

          if (selectedSuggestion.value?.suggestionId == updatedSuggestion.suggestionId) {
            _selectedSuggestion.value = updatedSuggestion
          }

          // Now check if the suggestion should be added to stops
          checkAndAddSuggestionAsStop(updatedSuggestion, allUsers, threshold)

          _isLikeChanging.value = false
        }
      }
    }
  }

  /**
   * Adds a comment to the suggestion and updates the backend and local state accordingly.
   *
   * @param suggestion The suggestion to which the comment belongs.
   * @param comment The new comment to be added.
   */
  open fun addComment(suggestion: Suggestion, comment: Comment) {
    val updatedSuggestion =
        suggestion.copy(
            comments =
                suggestion.comments +
                    Comment(
                        commentId = UUID.randomUUID().toString(),
                        userId = currentLoggedInUId,
                        userName = SessionManager.getCurrentUser()?.name!!,
                        text = comment.text,
                        createdAt = comment.createdAt,
                        createdAtTime = LocalTime.now()))
    viewModelScope.launch {
      val wasUpdateSuccessful =
          suggestionRepository?.updateSuggestionInTrip(tripId, updatedSuggestion)!!
      if (wasUpdateSuccessful) {
        loadSuggestion(tripId)
      }
    }
  }

  /**
   * Updates the comment in the suggestion and updates the backend and local state accordingly.
   *
   * @param suggestion The suggestion to which the comment belongs.
   * @param comment The updated comment.
   */
  open fun updateComment(suggestion: Suggestion, comment: Comment) {
    val updatedSuggestion =
        suggestion.copy(
            comments = suggestion.comments.filter { it.commentId != comment.commentId } + comment)
    viewModelScope.launch {
      val wasUpdateSuccessful =
          suggestionRepository?.updateSuggestionInTrip(tripId, updatedSuggestion)!!
      if (wasUpdateSuccessful) {
        loadSuggestion(tripId)
      }
    }

    _editingComment.value = false
  }

  open fun showBottomSheet(comment: Comment) {
    viewModelScope.launch {
      _bottomSheetVisible.value = true
      _selectedComment.value = comment
    }
  }

  open fun hideBottomSheet() {
    _bottomSheetVisible.value = false
  }

  open fun deleteComment(suggestion: Suggestion) {
    // delete selected comment logic
    val updatedSuggestion =
        suggestion.copy(comments = suggestion.comments - _selectedComment.value!!)
    viewModelScope.launch {
      val wasUpdateSuccessful =
          suggestionRepository?.updateSuggestionInTrip(tripId, updatedSuggestion)!!
      if (wasUpdateSuccessful) {
        loadSuggestion(tripId)
      }
    }
    _selectedComment.value = null
    hideBottomSheet()
  }

  open fun showDeleteDialog() {
    _showDeleteDialog.value = true
  }

  open fun hideDeleteDialog() {
    _showDeleteDialog.value = false
  }

  open fun confirmDeleteComment(suggestion: Suggestion) {
    deleteComment(suggestion) // Assuming deleteComment handles all necessary logic
    cancelEditComment()
    hideDeleteDialog()
    hideBottomSheet()
  }

  /**
   * Checks if a suggestion of a trip has reached the majority of likes and adds it as a stop if so.
   *
   * @param suggestion The suggestion of a trip to check and add as a stop.
   */
  open fun checkAndAddSuggestionAsStop(
      suggestion: Suggestion,
      allUsers: List<User>,
      threshold: Int
  ) {
    viewModelScope.launch {
      val likesCount = suggestion.userLikes.size

      val isMajority =
          if (allUsers.size % 2 == 1) {
            likesCount >=
                threshold // If the number of users is odd, the threshold of likes is the middle
            // user to ensure majority
          } else {
            likesCount >
                threshold +
                    1 // If the number of users is even, the threshold of likes is the middle two
            // users (one extra) to ensure majority
          }

      if (isMajority && !_addedSuggestionsToStops.value.contains(suggestion.suggestionId)) {
        val wasStopAdded = suggestionRepository?.addStopToTrip(tripId, suggestion.stop) ?: false
        if (wasStopAdded) {
          // Remove the suggestion from the state
          _state.value = _state.value.filterNot { it.suggestionId == suggestion.suggestionId }
          // Add the suggestion ID to the list of added stops
          _addedSuggestionsToStops.value += suggestion.suggestionId
          // Remove the suggestion from the trip's suggestion list
          suggestionRepository?.removeSuggestionFromTrip(tripId, suggestion.suggestionId)
          NotificationsManager.removeSuggestionPath(tripId, suggestion.suggestionId)
          NotificationsManager.addStopNotification(tripId, suggestion.stop)
          if (selectedSuggestion.value?.suggestionId == suggestion.suggestionId) {
            _selectedSuggestion.value = null
          }
        }
      }
    }
  }

  open fun deleteSuggestion(suggestion: Suggestion) {
    viewModelScope.launch {
      NotificationsManager.removeSuggestionPath(tripId, suggestion.suggestionId)
      val wasDeleteSuccessful =
          suggestionRepository?.removeSuggestionFromTrip(tripId, suggestion.suggestionId)!!
      if (wasDeleteSuccessful) {
        loadSuggestion(tripId)
      }
    }
  }

  open fun confirmDeleteSuggestion(suggestion: Suggestion) {
    deleteSuggestion(suggestion) // Assuming deleteComment handles all necessary logic
    hideDeleteDialog()
    hideBottomSheet()
  }

  open fun showSuggestionBottomSheet(suggestion: Suggestion) {
    viewModelScope.launch {
      _bottomSheetVisible.value = true
      _selectedSuggestion.value = suggestion
    }
  }

  open fun editCommentOption() {
    _editingComment.value = true
    hideBottomSheet()
  }

  open fun cancelEditComment() {
    _editingComment.value = false
  }

  open fun transformToStop(suggestion: Suggestion) {
    viewModelScope.launch {
      suggestionRepository?.addStopToTrip(tripId, suggestion.stop)
      NotificationsManager.removeSuggestionPath(tripId, suggestion.suggestionId)
      NotificationsManager.addStopNotification(tripId, suggestion.stop)
      suggestionRepository?.removeSuggestionFromTrip(tripId, suggestion.suggestionId)
    }
    loadSuggestion(tripId)
    hideBottomSheet()
  }

  class SuggestionsViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(SuggestionsViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return SuggestionsViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
