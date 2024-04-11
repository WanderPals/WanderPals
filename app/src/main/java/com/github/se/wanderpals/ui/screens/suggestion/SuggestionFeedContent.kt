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
    navigationActions: NavigationActions,
    suggestionList: List<Suggestion>, // <-todo: will be real data (wait for William) so will replace all _suggestionList by suggestionList
    searchSuggestionText: String,
    tripId: String,
    suggestionRepository: SuggestionsViewModel
) {
  // State to track the currently selected suggestion item
  var selectedSuggestion by remember { mutableStateOf<Suggestion?>(null) }

  // dummy data:
  val stop1 =
      Stop(
          stopId = "OSK001",
          title = "Osaka Castle",
          address = "1-1 Osakajo, Chuo Ward, Osaka, 540-0002, Japan",
          date = LocalDate.of(2024, 4, 10),
          startTime = LocalTime.of(9, 0),
          duration = 120,
          budget = 600.0,
          description = "Osaka Castle is one of Japan's most famous landmarks...",
          geoCords = GeoCords(latitude = 34.687315, longitude = 135.526201),
          website = "https://www.osakacastle.net/",
          imageUrl = "")
  val stop2 =
      Stop(
          stopId = "OSK002",
          title = "Dotonbori",
          address = "Dotonbori, Chuo Ward, Osaka, 542-0071, Japan",
          date = LocalDate.of(2024, 4, 10),
          startTime = LocalTime.of(18, 0), // Best experienced in the evening
          duration = 180, // Approximately 3 hours
          budget = 3000.0, // Food, shopping, and other activities
          description =
              "Dotonbori is Osaka's most famous tourist destination, known for its bright neon lights, extravagant signage, and abundant dining options.",
          geoCords = GeoCords(latitude = 34.668723, longitude = 135.501295),
          website = "",
          imageUrl = "")
  val stop3 =
      Stop(
          stopId = "OSK003",
          title = "Umeda Sky Building",
          address = "",
          date = LocalDate.of(2024, 4, 11),
          startTime = LocalTime.of(10, 30), // Opens at 10:30 AM
          duration = 90, // 1.5 hours visit
          budget = 1500.0, // Entrance fee and other possible expenses
          description =
              "The Umeda Sky Building is a spectacular high rise building in the Kita district of Osaka, featuring a futuristic observatory, the Floating Garden.",
          geoCords = GeoCords(latitude = 34.705938, longitude = 135.490357),
          website = "http://www.kuchu-teien.com/",
          imageUrl = "")
  val stop4 =
      Stop(
          stopId = "OSK004",
          title = "Umeda Sky Building 2",
          address = "",
          date = LocalDate.of(2024, 4, 11),
          startTime = LocalTime.of(10, 30), // Opens at 10:30 AM
          duration = 90, // 1.5 hours visit
          budget = 1500.0, // Entrance fee and other possible expenses
          description =
              "The Umeda Sky Building is a spectacular high rise building in the Kita district of Osaka, featuring a futuristic observatory, the Floating Garden.",
          geoCords = GeoCords(latitude = 34.705938, longitude = 135.490357),
          website = "",
          imageUrl = "")

  val comment1 = Comment("commentId1", "usercmtId1", "userNamecmt1", "Great idea!", LocalDate.now())
  val comment2 =
      Comment(
          "commentId2",
          "usercmtId2",
          "userNamecmt2",
          "I've been here before, it's wonderful.",
          LocalDate.now())
  val comment3 =
      Comment(
          "commentId3", "usercmtId3", "userNamecmt3", "This fits our schedule.", LocalDate.now())
  val comment4 =
      Comment(
          "commentId4", "usercmtId4", "userNamecmt4", "This place seems great.", LocalDate.now())

  // Example list of comments
    val dummyCommentList2 = listOf(comment1, comment2)
    val dummyCommentList3 = listOf(comment1, comment2, comment3)
    val dummyCommentList4 = listOf(comment1, comment2, comment3, comment4)

    val userLikes1 = listOf("ulId1", "ulId2")
    val userLikes3 = listOf("ulId1", "ulId2", "ulId3", "ulId5", "ulId6")
    val userLikes4 = listOf("ulId1", "ulId2", "ulId3", "ulId4")


//  /*
  // Use `this.suggestionList` to ensure we're assigning to the class-level variable.
      val _suggestionList =
          listOf(
              Suggestion(
                  "suggestionId1",
                  "userId1",
                  "userName1",
                  "Let us go here!",
                  LocalDate.of(2024, 1, 1),
                  stop1,
                  emptyList(),
                  userLikes1),
              Suggestion(
                  "suggestionId2",
                  "userId2",
                  "userName2",
                  "I love this place",
                  LocalDate.of(2024, 2, 2),
                  stop2,
                  dummyCommentList2,
                  emptyList()),
              Suggestion(
                  "suggestionId3",
                  "userId3",
                  "userName3",
                  "This is a great place to visit. Let us go here together! I am sure you will love it! I have been there before and it was amazing! " +
                          "Trying to convince you to go here with me. coz I know you will love it!",
                  LocalDate.of(2024, 3, 29),
                  stop3,
                  dummyCommentList3,
                  userLikes3),
              Suggestion(
                      "suggestionId4",
              "userId4",
              "userName4",
              "This is a great place to visit. Let us go here together! I am sure you will love it! I have been there before and it was amazing! " +
                      "Trying to convince you to go here with me. coz I know you will love it!",
              LocalDate.of(2024, 9, 29),
              stop4,
              dummyCommentList4,
              userLikes4)
          )
//    */

  // Example usage of dummy data for the suggestionList <-todo: change for sprint3
//  val _suggestionList = suggestionList


    // State to track the selected filter criteria
    var selectedFilterCriteria by remember { mutableStateOf("Creation date") }

    // State to track the sorted suggestion list
    val filteredSuggestionList by remember(selectedFilterCriteria) {
        mutableStateOf(
            when (selectedFilterCriteria) {
                "Like number" -> _suggestionList.sortedByDescending { it.userLikes.size } // Assuming you have likeCount in Suggestion data class
                "Comment number" -> _suggestionList.sortedByDescending { it.comments.size } // Assuming you have commentList in Suggestion data class
                else -> _suggestionList.sortedByDescending { it.createdAt }
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
            val isLiked = suggestionRepository.likedSuggestions.collectAsState().value.contains(suggestion.suggestionId)
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
            if (_suggestionList.isEmpty()) {
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
                            navigationActions = navigationActions,
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
