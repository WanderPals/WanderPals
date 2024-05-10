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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.theme.primaryLight
import com.github.se.wanderpals.ui.theme.scrimLight
import java.time.LocalDateTime

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
fun SuggestionHistoryFeedContent(
    innerPadding: PaddingValues,
    suggestionList: List<Suggestion>,
    searchSuggestionText: String,
    tripId: String,
    suggestionsViewModel: SuggestionsViewModel,
    navigationActions: NavigationActions
) {
    // Combine date and time into LocalDateTime and sort the list in descending order
    val sortedSuggestionHistoryList = suggestionList.sortedByDescending {
        LocalDateTime.of(it.createdAt, it.createdAtTime)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {

        // Title for the list of suggestions
        Text(
            text = "Suggestion History",
            modifier = Modifier.padding(start = 27.dp, top = 15.dp),
            style =
            TextStyle(
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(500),
                color = primaryLight,
                letterSpacing = 0.2.sp),
            textAlign = TextAlign.Center)

        // If suggestion list is empty, display a message
        if (sortedSuggestionHistoryList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier =
                    Modifier.width(260.dp)
                        .height(55.dp)
                        .align(Alignment.Center)
                        .testTag("noSuggestionsForUserText"),
                    text = "Looks like there is no suggestion history yet. ",
                    style =
                    TextStyle(
                        lineHeight = 20.sp,
                        letterSpacing = 0.5.sp,
                        fontSize = 18.sp,
                        fontWeight = FontWeight(500),
                        textAlign = TextAlign.Center,
                        color = scrimLight),
                )
                IconButton(
                    onClick = { suggestionsViewModel.loadSuggestion(tripId) },
                    modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
                    content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh suggestion History") })
            }
        } else {
            // LazyColumn to display the list of suggestions with sorting and search filtering
            // (Note: can only have one LazyColumn in a composable function)
            val lazyColumn =
                @Composable {
                    LazyColumn(modifier = Modifier.testTag("suggestionHistoryFeedContentList")) {
                        itemsIndexed(sortedSuggestionHistoryList) { index, suggestion ->
                            // Only render items that have not been added to stops
                            val addedToStops =
                                suggestionsViewModel.addedSuggestionsToStops.collectAsState().value
                            val isSuggestionAddedToStop = addedToStops.contains(suggestion.suggestionId)
                            if (isSuggestionAddedToStop) {
                                SuggestionItem(
                                    suggestion = suggestion,
                                    onClick = {
                                        navigationActions.setVariablesSuggestion(suggestion)
                                        navigationActions.navigateTo(Route.SUGGESTION_HISTORY)
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
    }
}
