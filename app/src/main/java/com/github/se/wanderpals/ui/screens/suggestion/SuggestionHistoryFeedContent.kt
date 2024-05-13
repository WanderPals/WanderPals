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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.trip.agenda.CalendarUiState
import com.github.se.wanderpals.ui.theme.WanderPalsTheme
import com.github.se.wanderpals.ui.theme.primaryLight
import com.github.se.wanderpals.ui.theme.scrimLight
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime

/**
 * The Suggestion feed screen content of a trip. A popup is displayed when a suggestion item is
 * selected.
 *
 * @param tripId The ID of the trip.
 * @param suggestionsViewModel The ViewModel for managing suggestions.
 * @param navigationActions The navigation actions for the screen.
 */
@Composable
fun SuggestionHistoryFeedContent(
    tripId: String,
    suggestionsViewModel: SuggestionsViewModel,
    navigationActions: NavigationActions
) {

    // Observe the state of the suggestions list from the ViewModel
    val suggestions = suggestionsViewModel.state.collectAsState().value

    // Filter suggestions that have been added as stops
    val addedSuggestions = suggestions.filter {
        it.stopStatus == CalendarUiState.StopStatus.ADDED
    }

    Column(modifier = Modifier
        .fillMaxWidth()) {

        // Title for the list of suggestions
        Text(
            text = "Suggestion History",
            modifier = Modifier.padding(start = 27.dp, top = 15.dp),
            style =
            TextStyle(
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(500),
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.2.sp),
            textAlign = TextAlign.Center)

        // Trigger data fetch when selectedDate changes
        LaunchedEffect(tripId) { suggestionsViewModel.loadSuggestion(tripId) }

        // While waiting for the data to load, display a loading spinner:
        val isLoading by suggestionsViewModel.isLoading.collectAsState()
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier =
                    Modifier
                        .width(260.dp)
                        .height(55.dp)
                        .align(Alignment.Center)
                        .testTag("suggestionHistoryLoading"),
                    text = "Loading...",
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
                    onClick = { suggestionsViewModel.loadSuggestion(tripId) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 60.dp),
                    content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh suggestion History") })
            }
        }

        // If suggestion list is empty, display a message
        if (addedSuggestions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier =
                    Modifier
                        .width(260.dp)
                        .height(55.dp)
                        .align(Alignment.Center)
                        .testTag("noSuggestionsForUserText"),
                    text = "No added stops yet. ",
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
                    onClick = { suggestionsViewModel.loadSuggestion(tripId) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 60.dp),
                    content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh suggestion History") })
            }
        } else {
            // this lazycolumn has all the suggestions that are added to stops
            val lazyColumn =
                @Composable {
                    LazyColumn(modifier = Modifier.testTag("suggestionHistoryFeedContentList")) {
                        itemsIndexed(addedSuggestions) { index, suggestion ->
                            if (suggestion.stopStatus== CalendarUiState.StopStatus.ADDED) {

                                SuggestionItem(
                                    suggestion = suggestion,
                                    onClick = {
                                        navigationActions.setVariablesSuggestion(suggestion)
                                        navigationActions.navigateTo(Route.SUGGESTION_DETAIL) // Navigate to the suggestion detail screen
                                    }, // This lambda is passed to the SuggestionItem composable
                                    modifier = Modifier.testTag("suggestion${index + 1}"),
                                    tripId = tripId,
                                    viewModel = suggestionsViewModel
                                )
                            }
                        }
                    }
                }
            PullToRefreshLazyColumn(
                inputLazyColumn = lazyColumn, onRefresh = { suggestionsViewModel.loadSuggestion(tripId) })
        }
    }
}
