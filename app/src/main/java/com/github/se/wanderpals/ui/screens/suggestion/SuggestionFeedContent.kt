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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route

/**
 * The Suggestion feed screen content of a trip. A popup is displayed when a suggestion item is
 * selected.
 *
 * @param innerPadding The padding values for the content. view.
 * @param suggestionList The list of suggestions of a trip to be displayed.
 * @param searchSuggestionText The text used for filtering suggestions of a trip by title.
 * @param tripId The ID of the trip.
 * @param suggestionsViewModel The ViewModel for managing suggestions.
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
  // State to track the currently selected suggestion item
  var selectedSuggestion by remember { mutableStateOf<Suggestion?>(null) }

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

    // Title for the list of suggestions
    Text(
        text = "Suggestions",
        modifier = Modifier.padding(start = 27.dp, top = 15.dp),
        style =
            TextStyle(
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF5A7BF0),
                letterSpacing = 0.5.sp),
        textAlign = TextAlign.Center)

    // Add the filter options UI
    Text(
        text = "Filter by:",
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
    )

    SuggestionFilterOptions { selectedCriteria -> selectedFilterCriteria = selectedCriteria }

    // When a suggestion is selected, display the detail screen
    selectedSuggestion?.let { suggestion ->
      navigationActions.setVariablesSuggestionId(suggestion.suggestionId)
      navigationActions.navigateTo(Route.SUGGESTION_DETAIL)
    }

    // If suggestion list is empty, display a message
    if (suggestionList.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier =
                Modifier.width(260.dp)
                    .height(55.dp)
                    .align(Alignment.Center)
                    .testTag("noSuggestionsForUserText"),
            text = "Looks like there is no suggestions yet. ",
            style =
                TextStyle(
                    lineHeight = 20.sp,
                    letterSpacing = 0.5.sp,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF000000)),
        )
      }
    } else {
      // LazyColumn to display the list of suggestions with sorting and search filtering
      // (Note: can only have one LazyColumn in a composable function)
      LazyColumn(modifier = Modifier.testTag("suggestionFeedContentList")) {
        itemsIndexed(displayList) { index, suggestion ->
          // Only render items that have not been added to stops
          val addedToStops = suggestionsViewModel.addedSuggestionsToStops.collectAsState().value
          val isSuggestionAddedToStop = addedToStops.contains(suggestion.suggestionId)
          if (!isSuggestionAddedToStop) {
            SuggestionItem(
                suggestion = suggestion,
                onClick = {
                  selectedSuggestion = suggestion
                }, // This lambda is passed to the SuggestionItem composable
                modifier = Modifier.testTag("suggestion${index + 1}"),
                tripId = tripId,
                viewModel = suggestionsViewModel)
          }
        }
      }
    }

    SuggestionBottomSheet(
        viewModel = suggestionsViewModel,
        onEdit = {
          navigationActions.setVariablesSuggestion(it)
          navigationActions.navigateTo(Route.CREATE_SUGGESTION)
        })
  }
}
