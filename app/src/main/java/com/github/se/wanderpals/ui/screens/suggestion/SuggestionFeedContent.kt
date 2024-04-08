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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.ui.navigation.NavigationActions

/**
 * The Suggestion feed screen content of a trip.
 *
 * @param innerPadding The padding values for the content.
 * @param navigationActions The navigation actions used for navigating to a detailed suggestion
 *   view.
 * @param suggestionList The list of suggestions of a trip to be displayed.
 * @param searchText The text used for filtering trips by title. <-todo: for sprint3
 */
@Composable
fun SuggestionFeedContent(
    innerPadding: PaddingValues,
    navigationActions: NavigationActions,
    suggestionList: List<Suggestion>, // <-todo: will be real data (wait for William)
    searchText: String
) {

  //    // Filter suggestions of a trip by userName based on search text todo: for sprint3
  //    val filteredSuggestionsByTitle =
  //        if (searchText.isEmpty()) {
  //            suggestionList
  //        } else {
  //            suggestionList.filter { suggestion ->
  // suggestion.userName.lowercase().contains(searchText.lowercase()) }
  //        }

  // Example usage of dummy data for the suggestionList <-todo: change for sprint3
  val _suggestionList = suggestionList

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
                  color = Color(0xFF000000)),
      )
    }
  } else {
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
                  letterSpacing = 0.5.sp),
          textAlign = TextAlign.Center)
      // LazyColumn to display the list of suggestions of a trip
      LazyColumn() {
        itemsIndexed(_suggestionList /*filteredSuggestionsByTitle todo: for sprint3*/) {
            index,
            suggestion ->
          SuggestionItem(
              suggestion = suggestion,
              navigationActions = navigationActions,
              modifier = Modifier.testTag("suggestion${index + 1}") // Apply the testTag here
              )
        }
      }
    }
  }
}
