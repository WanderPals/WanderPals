package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.suggestion.CommentBottomSheet
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionBottomBar
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionTopBar

/**
 * The Suggestion screen.
 *
 * @param oldNavActions The navigation actions of the button that was clicked to navigate to the
 *   screen.
 * @param tripId The ID of the trip.
 * @param suggestionsViewModel The ViewModel for managing suggestions.
 */
@Composable
fun Suggestion(
    oldNavActions: NavigationActions,
    tripId: String,
    suggestionsViewModel: SuggestionsViewModel,
    onSuggestionClick: () -> Unit
) {

  // get the suggestion list from the firebase database
  val suggestionList by suggestionsViewModel.state.collectAsState()

  // State for managing search suggestion text (the filter)
  var searchSuggestionText by remember { mutableStateOf("") }

  // State for managing the loading state
  val isLoading by suggestionsViewModel.isLoading.collectAsState()

  // State for managing the displaying of the bottom sheet
  val bottomSheetVisible by suggestionsViewModel.bottomSheetVisible.collectAsState()

  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(
          modifier = Modifier.size(50.dp).align(Alignment.Center).testTag("loading"))
    }
  } else {
    Scaffold(
        modifier = Modifier.testTag("suggestionFeedScreen"),
        topBar = {
          // Top bar with search functionality based on the title of the trips
          SuggestionTopBar(
              searchSuggestionText = searchSuggestionText,
              onSearchSuggestionTextChanged = { newSearchSuggestionText ->
                searchSuggestionText = newSearchSuggestionText
              })
        },
        bottomBar = { SuggestionBottomBar(onSuggestionClick = onSuggestionClick) }) { innerPadding
          ->
          SuggestionFeedContent(
              innerPadding = innerPadding,
              suggestionList = suggestionList,
              searchSuggestionText = searchSuggestionText,
              tripId = tripId,
              suggestionsViewModel = suggestionsViewModel)
          if (bottomSheetVisible) {
            CommentBottomSheet(onDismiss = { suggestionsViewModel.hideBottomSheet() })
          }
        }
  }
}
