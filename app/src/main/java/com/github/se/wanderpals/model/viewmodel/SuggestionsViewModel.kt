package com.github.se.wanderpals.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.service.NotificationsManager
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import java.time.LocalDateTime
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

/**
 * The ViewModel class for managing suggestions.
 *
 * @property suggestionRepository The repository for managing suggestions.
 * @property tripId The ID of the trip to manage suggestions for.
 */
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

  private val _voteIconClicked = MutableStateFlow<List<String>>(emptyList())

  // This will hold the IDs of suggestions that have been added to stops
  private val _addedSuggestionsToStops = MutableStateFlow<List<String>>(emptyList())
  open val addedSuggestionsToStops: StateFlow<List<String>> = _addedSuggestionsToStops.asStateFlow()

  // stores the suggestionId with its start time when the vote icon is clicked
  private val _voteStartTimeMap = mutableMapOf<String, LocalDateTime>()
  /**
   * Returns whether the suggestion is liked by the current user.
   *
   * @param suggestionId The ID of the suggestion to check.
   * @return True if the suggestion is liked, false otherwise.
   */
  open fun getIsLiked(suggestionId: String): Boolean {
    return _likedSuggestions.value.contains(suggestionId)
  }

  /**
   * Returns whether the vote icon has been clicked for a suggestion.
   *
   * @param suggestionId The ID of the suggestion to check.
   * @return True if the vote icon has been clicked, false if the icon is no longer clickable
   *   because it has been clicked.
   */
  open fun getVoteIconClicked(suggestionId: String): Boolean {
    return _voteIconClicked.value.contains(suggestionId)
  }

  /**
   * Returns the number of likes for a suggestion.
   *
   * @param suggestionId The ID of the suggestion to get the number of likes for.
   * @return The number of likes for the suggestion.
   */
  open fun getNbrLiked(suggestionId: String): Int {
    return _state.value.find { it.suggestionId == suggestionId }?.userLikes?.size ?: 0
  }

  /**
   * Fetches all trips from the repository and updates the state flow accordingly.
   *
   * @param tripId The ID of the trip to fetch suggestions for.
   */
  open fun loadSuggestion(tripId: String) {
    viewModelScope.launch {
      _isLoading.value = true

      // Fetch all trips from the repository
      delay(1000)
      val suggestions = suggestionRepository?.getAllSuggestionsFromTrip(tripId)!!
      _state.value = suggestions

      // Update the liked suggestions list with the current user's liked suggestions
      _likedSuggestions.value =
          _state.value.filter { it.userLikes.contains(currentLoggedInUId) }.map { it.suggestionId }

      _voteIconClicked.value =
          _state.value
              .filter { it.voteIconClicked }
              .map {
                it.suggestionId
              } // get the list of suggestions that have the vote icon that have been clicked

      _isLoading.value = false
    }
  }

  /**
   * Sets the selected suggestion to be displayed in the bottom sheet.
   *
   * @param suggestion The suggestion to be displayed.
   */
  open fun setSelectedSuggestion(suggestion: Suggestion) {
    _selectedSuggestion.value = suggestion
  }

  /**
   * Toggles the like status of a suggestion and updates the backend and local state accordingly.
   *
   * @param suggestion The suggestion to toggle the like status for.
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
   * Returns the remaining time for a suggestion to reach the majority of likes.
   *
   * @param suggestionId The ID of the suggestion to get the remaining time for.
   * @return The remaining time for the suggestion to reach the majority of likes.
   */
  open fun getRemainingTimeFlow(suggestionId: String): MutableStateFlow<String> {
    val startTime = _voteStartTimeMap[suggestionId]
    val remainingTimeFlow = MutableStateFlow("23:59:59")

    startTime?.let {
      val now = LocalDateTime.now()
      val endTime = it.plusHours(24)
      val duration = java.time.Duration.between(now, endTime)

      if (!duration.isNegative) {
        val hours = duration.toHours().toString().padStart(2, '0')
        val minutes = (duration.toMinutes() % 60).toString().padStart(2, '0')
        val seconds = (duration.seconds % 60).toString().padStart(2, '0')
        remainingTimeFlow.value = "$hours:$minutes:$seconds"
      } else {
        remainingTimeFlow.value = "00:00:00"
      }
    }

    return remainingTimeFlow
  }

  /**
   * Toggles the vote icon clicked status of a suggestion and updates the backend and local state
   * accordingly.
   *
   * @param suggestion The suggestion to toggle the vote icon clicked status for.
   */
  open fun toggleVoteIconClicked(suggestion: Suggestion) {
    viewModelScope.launch {
      val currentSuggestion =
          suggestionRepository?.getSuggestionFromTrip(tripId, suggestion.suggestionId)!!

      // Check if the vote icon is not clicked
      if (!getVoteIconClicked(currentSuggestion.suggestionId)) {
        // Store the start time when the vote icon is clicked
        val startTime = LocalDateTime.now()

        // Add the suggestion ID to the list of clicked vote icons
        _voteIconClicked.value += currentSuggestion.suggestionId

        // Prepare the updated suggestion for backend update
        val updatedSuggestion =
            currentSuggestion.copy(voteIconClicked = true, voteStartTime = startTime)

        val wasUpdateSuccessful =
            suggestionRepository.updateSuggestionInTrip(tripId, updatedSuggestion)

        if (wasUpdateSuccessful) {
          _state.value = suggestionRepository.getAllSuggestionsFromTrip(tripId)
          _voteIconClicked.value =
              _state.value.filter { it.voteIconClicked }.map { it.suggestionId }
          if (selectedSuggestion.value?.suggestionId == updatedSuggestion.suggestionId) {
            _selectedSuggestion.value = updatedSuggestion
          }
        }
      }
    }
  }

  /**
   * Returns the start time of a suggestion.
   *
   * @param suggestionId The ID of the suggestion to get the start time for.
   * @return The start time of the suggestion.
   */
  open fun getStartTime(suggestionId: String): LocalDateTime? {
    val suggestion = _state.value.find { it.suggestionId == suggestionId }
    return suggestion?.voteStartTime
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

  /**
   * Shows the bottom sheet with the selected comment.
   *
   * @param comment The comment to be displayed in the bottom sheet.
   */
  open fun showBottomSheet(comment: Comment) {
    viewModelScope.launch {
      _bottomSheetVisible.value = true
      _selectedComment.value = comment
    }
  }

  /** Hides the bottom sheet. */
  open fun hideBottomSheet() {
    _bottomSheetVisible.value = false
  }

  /**
   * Deletes the selected comment from the suggestion and updates the backend and local state
   * accordingly.
   *
   * @param suggestion The suggestion to which the comment belongs.
   */
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

  /** Shows the delete dialog. */
  open fun showDeleteDialog() {
    _showDeleteDialog.value = true
  }

  /** Hides the delete dialog. */
  open fun hideDeleteDialog() {
    _showDeleteDialog.value = false
  }

  /**
   * Confirms the deletion of the selected comment.
   *
   * @param suggestion The suggestion to which the comment belongs.
   */
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
   * @param allUsers The list of all users in the trip.
   * @param threshold The threshold for majority of likes.
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

      if (isMajority &&
          !_addedSuggestionsToStops.value.contains(
              suggestion
                  .suggestionId)) { // If the suggestion has reached the majority of likes and has
        // not been added as a stop yet

        val wasStopAdded = suggestionRepository?.addStopToTrip(tripId, suggestion.stop) ?: false
        if (wasStopAdded) {
          // Update suggestion with ADDED stopStatus
          val updatedStop = suggestion.stop.copy(stopStatus = CalendarUiState.StopStatus.CURRENT)
          val updatedSuggestion = suggestion.copy(stop = updatedStop)

          // Update the state flow list to include this updated suggestion
          _state.value =
              _state.value.map {
                if (it.suggestionId == suggestion.suggestionId) updatedSuggestion else it
              }

          suggestionRepository?.updateSuggestionInTrip(tripId, updatedSuggestion)

          // Remove the suggestion from the trip's suggestion list
          _state.value = _state.value.filterNot { it.suggestionId == suggestion.suggestionId }
          // Add the suggestion ID to the list of added stops
          _addedSuggestionsToStops.value += suggestion.suggestionId

          NotificationsManager.removeSuggestionPath(tripId, suggestion.suggestionId)
          NotificationsManager.addStopNotification(tripId, suggestion.stop)
          if (selectedSuggestion.value?.suggestionId == suggestion.suggestionId) {
            _selectedSuggestion.value = null
          }
        }
      }
    }
  }

  /**
   * Deletes a suggestion from the trip and updates the backend and local state accordingly.
   *
   * @param suggestion The suggestion to be deleted.
   */
  open fun deleteSuggestion(suggestion: Suggestion) {
    viewModelScope.launch {
      NotificationsManager.removeSuggestionPath(tripId, suggestion.suggestionId)
      val wasDeleteSuccessful =
          suggestionRepository?.removeSuggestionFromTrip(tripId, suggestion.suggestionId)!!
      if (wasDeleteSuccessful) {
        loadSuggestion(tripId) // Reload the suggestions list
      }
    }
  }

  /**
   * Confirms the deletion of the selected suggestion.
   *
   * @param suggestion The suggestion to be deleted.
   */
  open fun confirmDeleteSuggestion(suggestion: Suggestion) {
    deleteSuggestion(suggestion)
    hideDeleteDialog()
    hideBottomSheet()
  }

  /**
   * Shows the suggestion bottom sheet with the selected suggestion.
   *
   * @param suggestion The suggestion to be displayed with the bottom sheet.
   */
  open fun showSuggestionBottomSheet(suggestion: Suggestion) {
    viewModelScope.launch {
      _bottomSheetVisible.value = true
      _selectedSuggestion.value = suggestion
    }
  }

  /** Edits the comment option. */
  open fun editCommentOption() {
    _editingComment.value = true
    hideBottomSheet()
  }

  /** Cancels the edit comment option. */
  open fun cancelEditComment() {
    _editingComment.value = false
  }

  /**
   * Returns the role of the current user.
   *
   * @return The role of the current user.
   */
  open fun getCurrentUserRole(): Role {
    val currentUser = SessionManager.getCurrentUser()
    return currentUser?.role ?: Role.MEMBER
  }

  /**
   * Transforms a suggestion to a stop and updates the backend and local state accordingly.
   *
   * @param suggestion The suggestion to be transformed to a stop.
   */
  open fun transformToStop(suggestion: Suggestion) {
    viewModelScope.launch {
      suggestionRepository?.addStopToTrip(tripId, suggestion.stop)
      NotificationsManager.removeSuggestionPath(tripId, suggestion.suggestionId)
      NotificationsManager.addStopNotification(tripId, suggestion.stop)

      // Update suggestion with ADDED stopStatus
      val updatedStop = suggestion.stop.copy(stopStatus = CalendarUiState.StopStatus.CURRENT)
      val updatedSuggestion = suggestion.copy(stop = updatedStop)

      // Insert the updated suggestion at the beginning of the list
      _state.value =
          listOf(updatedSuggestion) +
              _state.value.filter { it.suggestionId != suggestion.suggestionId }

      suggestionRepository?.updateSuggestionInTrip(tripId, updatedSuggestion)
      // Add the suggestion ID to the list of added stops
      _addedSuggestionsToStops.value += suggestion.suggestionId
    }
    loadSuggestion(tripId)
    hideBottomSheet()
  }

  /**
   * Factory for creating a SuggestionsViewModel.
   *
   * @property tripsRepository The repository for managing trips.
   * @property tripId The ID of the trip to manage suggestions for.
   */
  class SuggestionsViewModelFactory(
      private val tripsRepository: TripsRepository,
      private val tripId: String
  ) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the SuggestionsViewModel.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return The created SuggestionsViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(SuggestionsViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return SuggestionsViewModel(tripsRepository, tripId) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
