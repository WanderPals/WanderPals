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
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionBottomBar
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionTopBar

/** The Suggestion screen. */
@Composable
fun Suggestion(tripId: String, suggestionsViewModel: SuggestionsViewModel) {
  val navController = rememberNavController()
  val navActions = NavigationActions(navController)

    // Collecting suggestions list and loading state from view model
  val suggestionList by suggestionsViewModel.state.collectAsState()
    val isLoading by suggestionsViewModel.isLoading.collectAsState()

  // State for managing search text (the filter) <-todo: for sprint3
  var searchSuggestionText by remember { mutableStateOf("") }


    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp).align(Alignment.Center))
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
            bottomBar = {
                SuggestionBottomBar(onSuggestionClick = { navActions.navigateTo(Route.CREATE_SUGGESTION) })
            }) { innerPadding ->
            //    NavHost(navController, startDestination = Route.DASHBOARD,
            // Modifier.padding(innerPadding))
            SuggestionFeedContent(
                innerPadding = innerPadding,
                navigationActions = navActions,
                suggestionList = suggestionList,
                searchSuggestionText = searchSuggestionText
            )
        }
    }
}
