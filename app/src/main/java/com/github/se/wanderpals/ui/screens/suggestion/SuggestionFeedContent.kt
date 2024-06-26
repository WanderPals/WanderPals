package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState

/**
 * The Suggestion feed screen content of a trip. A popup is displayed when a suggestion item is
 * selected.
 *
 * @param innerPadding The padding values for the content.
 * @param suggestionList The list of suggestions of a trip to be displayed.
 * @param searchSuggestionText The text used for filtering suggestions of a trip by title.
 * @param tripId The ID of the trip.
 * @param suggestionsViewModel The ViewModel for managing suggestions.
 * @param navigationActions The navigation actions used to navigate to different screens.
 */
@Composable
fun SuggestionFeedContent(
    innerPadding: PaddingValues,
    suggestionList: List<Suggestion>,
    searchSuggestionText: String,
    tripId: String,
    suggestionsViewModel: SuggestionsViewModel,
    navigationActions: NavigationActions
) {
  // State to track the selected filter criteria
  var selectedFilterCriteria by remember { mutableStateOf("Creation date") }

  // State to track the sorted suggestion list
  val filteredSuggestionList by
      remember(selectedFilterCriteria) {
        mutableStateOf(
            when (selectedFilterCriteria) {
              "Like number" -> suggestionList.sortedByDescending { it.userLikes.size }
              "Comment number" -> suggestionList.sortedByDescending { it.comments.size }
              else -> suggestionList.sortedByDescending { it.createdAt }
            })
      }

  // Apply the search filter if there is a search text
  val displayList =
      if (searchSuggestionText.isEmpty()) {
        filteredSuggestionList
      } else {
        filteredSuggestionList.filter { suggestion ->
          suggestion.stop.title.lowercase().contains(searchSuggestionText.lowercase())
        }
      }

  Column(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
    // Add the filter options UI
    Text(
        text = "Sort by:",
        modifier = Modifier.padding(start = 27.dp, top = 24.dp, end = 16.dp),
        style =
            TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(500),
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.16.sp,
            ))

    SuggestionFilterOptions { selectedCriteria -> selectedFilterCriteria = selectedCriteria }

    // If suggestion list is empty, display a message
    if (suggestionList.isEmpty() ||
        suggestionList.none {
          it.stop.stopStatus == CalendarUiState.StopStatus.NONE
        }) { // if there are no suggestions or if all suggestions are added to stops
      EmptyStateMessage(
          message =
              if (SessionManager.getIsNetworkAvailable()) {
                "Looks like there is no suggestions yet."
              } else {
                "Looks like you are offline. Check your connection."
              },
          onRefresh = { suggestionsViewModel.loadSuggestion(tripId) },
          testTag = "noSuggestionsForUserText",
          contentDescription = "Refresh suggestions",
          color = MaterialTheme.colorScheme.onSurface)
    } else {
      // LazyColumn to display the list of suggestions with sorting and search filtering
      // (Note: can only have one LazyColumn in a composable function)
      PullToRefreshLazyColumn(
          inputLazyColumn = {
            SuggestionList(
                suggestions = displayList,
                suggestionsViewModel = suggestionsViewModel,
                navigationActions = navigationActions,
                testTag = "suggestionFeedContentList")
          },
          onRefresh = { suggestionsViewModel.loadSuggestion(tripId) })
    }

    SuggestionBottomSheet(
        viewModel = suggestionsViewModel,
        onEdit = {
          navigationActions.setVariablesSuggestion(it)
          navigationActions.navigateTo(Route.CREATE_SUGGESTION)
        })
  }
}

/**
 * Composable function to display the list of suggestions.
 *
 * @param suggestions The list of suggestions to be displayed.
 * @param suggestionsViewModel The ViewModel for managing suggestions.
 * @param navigationActions The navigation actions used to navigate to different screens.
 * @param modifier The modifier for the list.
 * @param testTag The test tag for the list.
 */
@Composable
fun SuggestionList(
    suggestions: List<Suggestion>,
    suggestionsViewModel: SuggestionsViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    testTag: String
) {
  LazyColumn(modifier = modifier.testTag(testTag)) {
    itemsIndexed(suggestions) { index, suggestion ->
      val addedToStops = suggestionsViewModel.addedSuggestionsToStops.collectAsState().value
      val isSuggestionAddedToStop = addedToStops.contains(suggestion.suggestionId)
      if (!isSuggestionAddedToStop &&
          suggestion.stop.stopStatus ==
              CalendarUiState.StopStatus
                  .NONE) { // if the suggestion is not added to a stop (stopStatus is
        // NONE)
        SuggestionItem(
            suggestion = suggestion,
            onClick = {
              navigationActions.setVariablesSuggestion(suggestion)
              navigationActions.navigateTo(Route.SUGGESTION_DETAIL)
            },
            viewModel = suggestionsViewModel,
            modifier = Modifier.testTag("suggestion${index + 1}"))
      }
    }
  }
}
