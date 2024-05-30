package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionSearchBar
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionTopBar

/**
 * The Suggestion screen.
 *
 * @param oldNavActions The navigation actions of the button that was clicked to navigate to the
 *   screen.
 * @param tripId The ID of the trip.
 * @param suggestionsViewModel The ViewModel for managing suggestions.
 * @param onSuggestionClick The callback function for handling the suggestion button click.
 */
@Composable
fun Suggestion(
    oldNavActions: NavigationActions,
    tripId: String,
    suggestionsViewModel: SuggestionsViewModel,
    onSuggestionClick: () -> Unit
) {

  LaunchedEffect(Unit) {
    // Fetch all suggestions for the trip
    suggestionsViewModel.loadSuggestion(tripId)
  }

  // get the suggestion list from the firebase database
  val suggestionList by suggestionsViewModel.state.collectAsState()

  // State for managing search suggestion text (the filter)
  var searchSuggestionText by remember { mutableStateOf("") }

  // State for managing the loading state
  val isLoading by suggestionsViewModel.isLoading.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("suggestionFeedScreen"),
      topBar = {
        Column {
          SuggestionTopBar(onHistoryClick = { oldNavActions.navigateTo(Route.SUGGESTION_HISTORY) })
          Spacer(modifier = Modifier.padding(top = 4.dp))
          // Top bar with search functionality based on the title of the trips
          SuggestionSearchBar(
              searchSuggestionText = searchSuggestionText,
              onSearchSuggestionTextChanged = { newSearchSuggestionText ->
                searchSuggestionText = newSearchSuggestionText
              })
        }
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { onSuggestionClick() },
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.testTag("suggestionButtonExists")) {
              Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Add Suggestion",
                  modifier = Modifier.size(35.dp),
                  tint = MaterialTheme.colorScheme.onPrimary)
            }
      }) { innerPadding ->
        if (isLoading) {
          Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp).align(Alignment.Center).testTag("loading"))
          }
        } else {
          SuggestionFeedContent(
              innerPadding = innerPadding,
              suggestionList = suggestionList,
              searchSuggestionText = searchSuggestionText,
              tripId = tripId,
              suggestionsViewModel = suggestionsViewModel,
              navigationActions = oldNavActions)
        }
      }
}
