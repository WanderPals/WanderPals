package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionBottomBar
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionTopBar

/**
 * The Suggestion screen.
 *
 * @param oldNavActions The navigation actions of the button that was clicked to navigate to the
 *   screen.
 */
@Composable
fun Suggestion(
    oldNavActions: NavigationActions,
    tripId: String,
    suggestionsViewModel: SuggestionsViewModel
) {

    // get the suggestion list from the firebase database
    val suggestionList by suggestionsViewModel.state.collectAsState()

    // State for managing search suggestion text (the filter)
    var searchSuggestionText by remember { mutableStateOf("") }

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
        bottomBar = {
            SuggestionBottomBar(
                onSuggestionClick = { oldNavActions.navigateTo("${Route.CREATE_SUGGESTION}/$tripId") })
        }) { innerPadding ->
        SuggestionFeedContent(
            innerPadding = innerPadding,
            suggestionList = suggestionList,
            searchSuggestionText = searchSuggestionText,
            tripId = tripId,
            suggestionRepository = suggestionsViewModel)
    }
}
