package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState

/**
 * The Suggestion feed screen content of a trip. A popup is displayed when a suggestion item is
 * selected.
 *
 * @param suggestionsViewModel The ViewModel for managing suggestions.
 */
@Composable
fun SuggestionHistoryFeedContent(suggestionsViewModel: SuggestionsViewModel) {

  // Observe the state of the suggestions list from the ViewModel
  val suggestions = suggestionsViewModel.state.collectAsState().value

  // Filter suggestions that have been added as stops
  val addedSuggestions =
      suggestions
          .filter { it.stop.stopStatus == CalendarUiState.StopStatus.CURRENT }
          .asReversed() // Reverse the list to show the most recent suggestions history item first

  val tripId =
      suggestionsViewModel.tripId // Get the tripId of the trip from the SuggestionsViewModel

  Scaffold(
      topBar = {
        // Title for the list of suggestions
        GoBackSuggestionTopBar(
            title = "Suggestion History", onBack = { navigationActions.goBack() })
      }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxWidth()) {
          // Trigger data fetch when selectedDate changes
          LaunchedEffect(tripId) { suggestionsViewModel.loadSuggestion(tripId) }

          // While waiting for the data to load, display a loading spinner:
          val isLoading by suggestionsViewModel.isLoading.collectAsState()
          if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
              CircularProgressIndicator(
                  modifier =
                      Modifier.size(50.dp)
                          .align(Alignment.Center)
                          .testTag("suggestionHistoryLoadingSpinner"))
            }
          }

          // If suggestion list is empty, display a message
          if (addedSuggestions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
              Text(
                  modifier =
                      Modifier.width(260.dp)
                          .height(55.dp)
                          .align(Alignment.Center)
                          .testTag("noSuggestionsHistoryToDisplay"),
                  text =
                      when (SessionManager.getIsNetworkAvailable()) {
                        true -> "No stops in the history yet."
                        false -> "No internet connection"
                      },
                  style =
                      TextStyle(
                          lineHeight = 20.sp,
                          letterSpacing = 0.5.sp,
                          fontSize = 18.sp,
                          fontWeight = FontWeight(500),
                          textAlign = TextAlign.Center,
                          color = MaterialTheme.colorScheme.scrim),
              )
              IconButton(
                  enabled = SessionManager.getIsNetworkAvailable(),
                  onClick = { suggestionsViewModel.loadSuggestion(tripId) },
                  modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
                  content = {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh suggestion History")
                  })
            }
          } else {
            // this lazy column has all the suggestions that are added to stops
            val lazyColumn =
                @Composable {
                  LazyColumn(modifier = Modifier.testTag("suggestionHistoryFeedContentList")) {
                    itemsIndexed(addedSuggestions) { index, suggestion ->
                      if (suggestion.stop.stopStatus == CalendarUiState.StopStatus.CURRENT) {
                        // Add space between suggestionHistoryItems:
                        Spacer(modifier = Modifier.height(16.dp))

                        SuggestionHistoryItem(
                            suggestion = suggestion,
                            modifier = Modifier.testTag("suggestionHistory${index + 1}"),
                            viewModel = suggestionsViewModel)
                      }
                    }
                  }
                }
            PullToRefreshLazyColumn(
                inputLazyColumn = lazyColumn,
                onRefresh = { suggestionsViewModel.loadSuggestion(tripId) })
          }
        }
      }
}
