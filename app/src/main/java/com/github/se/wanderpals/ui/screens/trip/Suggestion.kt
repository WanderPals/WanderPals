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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionBottomBar
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionFeedContent
import com.github.se.wanderpals.ui.screens.suggestion.SuggestionTopBar

/** The Suggestion screen. */
@Composable
fun Suggestion(
    /*oldNavActions: NavigationActions,*/ tripId: String,
    suggestionsViewModel: SuggestionsViewModel,
    tripsRepository: TripsRepository,
    navActions: NavigationActions
) {
  //val navController = rememberNavController()
  //val navActions = NavigationActions(navController)

  //    val suggestionRepository: TripsRepository = TripsRepository()
  //    val suggestionsViewModel = SuggestionsViewModel(suggestionRepository, tripId)

  val suggestionList by
      suggestionsViewModel.state
          .collectAsState() // todo: use dummy data for now for testing functionality of the front
                            // end of the SuggestionFeedScreen

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
        SuggestionBottomBar(onSuggestionClick = { navActions.navigateTo(Route.CREATE_SUGGESTION) })
      }) { innerPadding ->
        //    NavHost(navController, startDestination = Route.DASHBOARD,
        // Modifier.padding(innerPadding))
        SuggestionFeedContent(
            innerPadding = innerPadding,
            navigationActions = navActions,
            suggestionList = suggestionList,
            searchSuggestionText = searchSuggestionText,
            tripId = tripId,
            suggestionRepository = suggestionsViewModel)
      }
}
