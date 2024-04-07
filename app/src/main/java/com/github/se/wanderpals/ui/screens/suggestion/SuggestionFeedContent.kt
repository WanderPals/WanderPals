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
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.ui.navigation.NavigationActions
import java.time.LocalDate
import java.time.LocalTime

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
  val stop1 =
      Stop(
          stopId = "OSK001",
          title = "Osaka Castle",
          address = "1-1 Osakajo, Chuo Ward, Osaka, 540-0002, Japan",
          date = LocalDate.of(2024, 4, 10), // Example date
          startTime = LocalTime.of(9, 0), // Opens at 9 AM
          duration = 120, // 2 hours visit
          budget = 600.0, // Entrance fee and other expenses
          description =
              "Osaka Castle is one of Japan's most famous landmarks and it played a major role in the unification of Japan during the sixteenth century.",
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
          website = "https://www.dotonbori.or.jp/en/",
          imageUrl = "")

  val stop3 =
      Stop(
          stopId = "OSK003",
          title = "Umeda Sky Building",
          address = "1-1-88 Oyodonaka, Kita Ward, Osaka, 531-0076, Japan",
          date = LocalDate.of(2024, 4, 11),
          startTime = LocalTime.of(10, 30), // Opens at 10:30 AM
          duration = 90, // 1.5 hours visit
          budget = 1500.0, // Entrance fee and other possible expenses
          description =
              "The Umeda Sky Building is a spectacular high rise building in the Kita district of Osaka, featuring a futuristic observatory, the Floating Garden.",
          geoCords = GeoCords(latitude = 34.705938, longitude = 135.490357),
          website = "http://www.kuchu-teien.com/",
          imageUrl = "")

  /*
  // these data are used for checking the UI, will be replaced with real data with William
  val _suggestionList =
      listOf(
          com.github.se.wanderpals.model.data.Suggestion(
              "suggestionId1",
              "userId1",
              "userName1",
              "Let us go here!",
              LocalDate.of(2024, 1, 1),
              stop1,
              emptyList(),
              emptyList()),
          com.github.se.wanderpals.model.data.Suggestion(
              "suggestionId2",
              "userId2",
              "userName2",
              "I love this place",
              LocalDate.of(2024, 2, 2),
              stop2,
              emptyList(),
              emptyList()),
          com.github.se.wanderpals.model.data.Suggestion(
              "suggestionId3",
              "userId3",
              "userName3",
              "This is a great place to visit." +
                  "Let us go here together!" +
                  "I am sure you will love it!" +
                  "I have been there before and it was amazing!" +
                  "Trying to convince you to go here with me." +
                  "coz I know you will love it!",
              LocalDate.of(2024, 3, 29),
              stop3,
              emptyList(),
              emptyList()))
      */
  val _suggestionList = suggestionList

  // If suggestion list is empty, display a message
  if (_suggestionList.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize()) {
      Text(
          modifier =
              Modifier.align(Alignment.Center)
                  .width(260.dp)
                  .height(55.dp)
                  //                  .width(200.dp) // Reduced width for smaller screens
                  //                  .height(50.dp) // Adjusted height for smaller screens
                  .testTag("noSuggestionsForUserText"),
          text = "Looks like there is no suggestions yet. ",
          style =
              TextStyle(
                  fontSize = 18.sp,
                  lineHeight = 20.sp,
                  fontWeight = FontWeight(500),
                  //                  fontSize = 16.sp, // Adjusted for smaller screen
                  //                  lineHeight = 18.sp, // Adjusted line height
                  //                  fontWeight = FontWeight.Medium, // Adjusted font weight for
                  // readability
                  color = Color(0xFF000000),
                  textAlign = TextAlign.Center,
                  letterSpacing = 0.5.sp,
                  //                  letterSpacing = 0.1.sp, // Slightly adjusted letter spacing
              ),
      )
    }
  } else {
    Column(modifier = Modifier.fillMaxWidth().padding(innerPadding))
    //      Column(modifier = Modifier
    //          .fillMaxWidth()
    //          .padding(top = innerPadding.calculateTopPadding() + 8.dp,
    //              bottom = innerPadding.calculateBottomPadding() + 8.dp,
    //              start = 8.dp,
    //              end = 8.dp))
    {
      // todo: for sprint3, filter
      //            // If no matching trips found, display a message
      //            if (filteredSuggestionsByTitle.isEmpty()) {
      //                Text(
      //                    text = "No trip found.",
      //                    modifier =
      //                    Modifier.align(Alignment.CenterHorizontally)
      //                        .padding(top = 20.dp)
      //                        .testTag("noTripFoundOnSearchText"),
      //                    style =
      //                    TextStyle(
      //                        fontSize = 16.sp,
      //                        lineHeight = 24.sp,
      //                        fontWeight = FontWeight(500),
      //                        color = Color.Gray,
      //                        letterSpacing = 0.5.sp,
      //                    ))
      //            } else {
      // Title for the list of suggestions
      Text(
          text = "Suggestions",
          modifier = Modifier.padding(start = 27.dp, top = 15.dp),
          //          modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp), //
          // Reduced padding
          style =
              TextStyle(
                  fontSize = 20.sp,
                  lineHeight = 24.sp,
                  fontWeight = FontWeight(500),
                  //                  fontSize = 18.sp, // Adjusted font size
                  //                  lineHeight = 22.sp, // Adjusted line height
                  //                  fontWeight = FontWeight.Medium, // Adjusted font weight
                  color = Color(0xFF5A7BF0),
                  textAlign = TextAlign.Center,
                  //                  letterSpacing = 0.5.sp,
                  letterSpacing = 0.4.sp, // Slightly adjusted letter spacing
              ),
          textAlign = TextAlign.Center,
      )
      // LazyColumn to display the list of suggestions of a trip
      LazyColumn() {
        itemsIndexed(_suggestionList /*filteredSuggestionsByTitle todo: for sprint3*/) {
            index,
            suggestion ->
          SuggestionItem(
              suggestion = suggestion,
              navigationActions = navigationActions,
              modifier =
                  Modifier.testTag("suggestion${index + 1}") // Apply the testTag here
                      .padding(bottom = 4.dp) // Reduced padding between items
              )
        }
      }
    }
  }
}

// todo: see overviewcontent.kt
