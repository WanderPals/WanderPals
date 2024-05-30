package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * @param innerPadding The padding values for the content. view.
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
      Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier =
                Modifier.width(260.dp)
                    .height(55.dp)
                    .align(Alignment.Center)
                    .testTag("noSuggestionsForUserText"),
            text =
                when {
                  SessionManager.getIsNetworkAvailable() ->
                      "Looks like there is no suggestions yet. "
                  else -> "Looks like you are offline. Check your connection."
                },
            style =
                TextStyle(
                    lineHeight = 20.sp,
                    letterSpacing = 0.5.sp,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface),
        )
        IconButton(
            enabled = SessionManager.getIsNetworkAvailable(),
            onClick = { suggestionsViewModel.loadSuggestion(tripId) },
            modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
            content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh suggestions") })
      }
    } else {
      // LazyColumn to display the list of suggestions with sorting and search filtering
      // (Note: can only have one LazyColumn in a composable function)
      val lazyColumn =
          @Composable {
            LazyColumn(modifier = Modifier.testTag("suggestionFeedContentList")) {
              itemsIndexed(displayList) { index, suggestion ->
                // Only render items that have not been added to stops
                val addedToStops =
                    suggestionsViewModel.addedSuggestionsToStops.collectAsState().value
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
                      }, // This lambda is passed to the SuggestionItem composable
                      modifier = Modifier.testTag("suggestion${index + 1}"),
                      tripId = tripId,
                      viewModel = suggestionsViewModel)
                }
              }
            }
          }
      PullToRefreshLazyColumn(
          inputLazyColumn = lazyColumn, onRefresh = { suggestionsViewModel.loadSuggestion(tripId) })
    }

    SuggestionBottomSheet(
        viewModel = suggestionsViewModel,
        onEdit = {
          navigationActions.setVariablesSuggestion(it)
          navigationActions.navigateTo(Route.CREATE_SUGGESTION)
        })
  }
}
