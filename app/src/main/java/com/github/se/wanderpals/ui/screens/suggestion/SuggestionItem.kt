package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.ui.navigation.NavigationActions
import java.time.format.DateTimeFormatter

/**
 * Composable function that represents a single suggestion item in the suggestion feed.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param navigationActions The navigation actions used for navigating to detailed suggestion view.
 *   todo: will be displayed later (overlay)
 * @param modifier The modifier to be applied to the suggestion item.
 */
@Composable
fun SuggestionItem(
    suggestion: Suggestion,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier // Add this line to accept a Modifier
) {
  // Define card colors with a white background
  val cardColors =
      CardDefaults.cardColors(
          containerColor = Color.White // This sets the background color of the Card
          )

  //    // Parent Box to allow alignment - Assuming the parent will fill its parent or have
  // specified size
  //    Box(
  //        modifier = modifier
  //                .width(360.dp) // Specify the width of the Card here
  //            .height(150.dp), // Specify the height of the Card here
  //         contentAlignment = Alignment.TopCenter // Aligns children at the top center
  //
  //    )
  Card(
      modifier =
          modifier
              .padding(8.dp) // Add padding around the Card
              //              .width(380.dp) // the width of the Card
              //              .height(166.dp) // the height of the Card
              .width(360.dp) // the width of the Card
              .height(120.dp), // the height of the Card
      //              .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(25.dp)),
      shape = RoundedCornerShape(size = 15.dp),
      colors = cardColors // Use the cardColors with the white background
      ) {
        //        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        //        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row( // Row for title and date
              //              modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
              modifier = Modifier.fillMaxWidth(), // .padding(bottom = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween) {
                //                Text(text = suggestion.stop.title, fontWeight = FontWeight.Bold,
                // fontSize = 18.sp)
                Text(text = suggestion.stop.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = suggestion.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontSize = 12.sp // Further reduced font size
                    )
              }

          Spacer(
              modifier =
                  //                  Modifier.height(4.dp)) // Add spacing between the first row
                  // and the second row
                  Modifier.height(2.dp))
          // the second row
          Text(
              text = suggestion.userName, // "Suggested by: ${suggestion.userName}",
              style = MaterialTheme.typography.bodyMedium,
              //              fontSize = 14.sp)
              fontSize = 12.sp)

          Spacer(
              modifier =
                  //                  Modifier.height(8.dp)) // Add spacing between the second row
                  // and the third row
                  Modifier.height(4.dp))
          // the third row
          Text(
              text = suggestion.text,
              style = MaterialTheme.typography.bodySmall,
              //              fontSize = 14.sp,
              fontSize = 12.sp,
              maxLines = 2, // Limit the text to two lines
              overflow = TextOverflow.Ellipsis // Add ellipsis if the text is longer than two lines
              )

          Spacer(modifier = Modifier.weight(1f)) // Pushes the icons to the bottom

          Row( // Row for comments and likes
              //              modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
              modifier = Modifier.fillMaxWidth(), // .padding(top = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                //                Spacer(Modifier.width(240.dp)) // Use the space to align the mail
                // icon
                Spacer(Modifier.width(180.dp)) // Use the space to align the mail icon

                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = null, // Decorative element
                    //                    modifier = Modifier.size(18.dp))
                    modifier = Modifier.size(16.dp))

                //                Spacer(modifier = Modifier.width(4.dp)) // Space between icon and
                // text
                Spacer(modifier = Modifier.width(2.dp)) // Space between icon and text

                Text(
                    text = "${suggestion.comments.size}",
                    //                    modifier = Modifier.padding(start = 4.dp)
                    fontSize = 12.sp)

                Spacer(modifier = Modifier.weight(1f)) // Pushes the heart icon to the end

                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null, // Decorative element
                    //                    modifier = Modifier.size(18.dp))
                    modifier = Modifier.size(16.dp))
                //              Spacer(modifier = Modifier.width(4.dp)) // Space between icon and
                // text
                Spacer(modifier = Modifier.width(2.dp)) // Space between icon and text
                Text(
                    text = "${suggestion.userLikes.size}",
                    //                    modifier = Modifier.padding(start = 4.dp)
                    fontSize = 12.sp)
              }
        }
      }
}

// @Composable
// fun SuggestionTrip(/*oldNavActions: NavigationActions, tripId: String*/) {
//  val navController = rememberNavController()
//  val navActions = NavigationActions(navController)
//    val suggestionsViewModel = SuggestionsViewModel()

// }

// todo: see OverviewTrip.kt

// todo: imo the size of the things inside the a suggestionItem don't need to be changed, but
//  the space between your input fields needs to be smaller.
// https://edstem.org/eu/courses/1193/discussion/94973
// todo: Also, remove all the paddings so the height of the card can be reduced
