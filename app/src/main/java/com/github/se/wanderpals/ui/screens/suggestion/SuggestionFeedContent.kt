package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.clickable
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
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import java.time.LocalDate
import java.time.LocalTime

/**
 * The Suggestion feed screen content of a trip. A popup is displayed when a suggestion item is
 * selected.
 *
 * @param innerPadding The padding values for the content.
 * @param navigationActions The navigation actions used for navigating to a detailed suggestion
 *   view.
 * @param suggestionList The list of suggestions of a trip to be displayed.
 * @param searchSuggestionText The text used for filtering suggestions of a trip by title.
 */
@Composable
fun SuggestionFeedContent(
    innerPadding: PaddingValues,
    suggestionList: List<Suggestion>, // <-todo: will be real data (wait for William) so will replace all _suggestionList by suggestionList
    searchSuggestionText: String,
    tripId: String,
    suggestionRepository: SuggestionsViewModel
) {
  // State to track the currently selected suggestion item
  var selectedSuggestion by remember { mutableStateOf<Suggestion?>(null) }

    // State to track the selected filter criteria
    var selectedFilterCriteria by remember { mutableStateOf("Creation date") }

    // State to track the sorted suggestion list
    val filteredSuggestionList by remember(selectedFilterCriteria) {
        mutableStateOf(
            when (selectedFilterCriteria) {
                "Like number" -> suggestionList.sortedByDescending { it.userLikes.size } // Assuming you have likeCount in Suggestion data class
                "Comment number" -> suggestionList.sortedByDescending { it.comments.size } // Assuming you have commentList in Suggestion data class
                else -> suggestionList.sortedByDescending { it.createdAt }
            }
        )
    }

    // Apply the search filter if there is a search text
    val displayList = if (searchSuggestionText.isEmpty()) {
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
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center
        )


        // Add the filter options UI
        Text(
            text = "Filter by:",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
        )

        SuggestionFilterOptions { selectedCriteria ->
            selectedFilterCriteria = selectedCriteria
        }

        // When a suggestion is selected, display the popup
        selectedSuggestion?.let { suggestion ->
            val isLiked = suggestionRepository.getIsLiked()//suggestionRepository.likedSuggestions.collectAsState().value.contains(suggestionRepository.currentLoggedInUId)
            val likesCount = suggestion.userLikes.size + if (isLiked) 1 else 0

            SuggestionDetailPopup(
                suggestion = suggestion,
                comments = suggestion.comments,
                isLiked = isLiked, // pass the current like status for the suggestion
                likesCount = likesCount, // pass the current number of likes for the suggestion
                onDismiss = { selectedSuggestion = null }, // When the popup is dismissed
                onLikeClicked = {
                    // Call toggleLikeSuggestion from the ViewModel
                    suggestionRepository.toggleLikeSuggestion(tripId, suggestion)
                }
            )
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
                            color = Color(0xFF000000)
                        ),
                    )
                }
            } else {
                // LazyColumn to display the list of suggestions with sorting and search filtering
                // (Note: can only have one LazyColumn in a composable function)
                LazyColumn {
                    itemsIndexed(displayList) { index, suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = {
                                selectedSuggestion = suggestion
                            }, // This lambda is passed to the SuggestionItem composable
                            modifier = Modifier.clickable { selectedSuggestion = suggestion }
                                .testTag("suggestion${index + 1}"), // Apply the testTag here

                            tripId = tripId,
                            viewModel = suggestionRepository

                        )
                    }
                }
    }
  }
}
