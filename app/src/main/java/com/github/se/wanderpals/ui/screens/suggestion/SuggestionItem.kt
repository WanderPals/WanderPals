package com.github.se.wanderpals.ui.screens.suggestion

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import java.time.format.DateTimeFormatter

/**
 * Composable function that represents a single suggestion item in the suggestion feed.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param modifier The modifier to be applied to the suggestion item.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun SuggestionItem(
    suggestion: Suggestion,
    onClick: () -> Unit, // this callback is for the suggestion item click
    tripId: String, // the trip id of the suggestion
    viewModel: SuggestionsViewModel,
    modifier: Modifier = Modifier // Add this line to accept a Modifier
) {

  // Collect the set of liked suggestion IDs and check if the current suggestion is liked
  val likedSuggestions = viewModel.likedSuggestions.collectAsState().value

  // State for the like status of the suggestion
  val isLiked = viewModel.getIsLiked()

  // State for the like count, which depends on the `userLikes` size
  // Calculate the like count dynamically based on whether the suggestion is liked
  val likesCount by derivedStateOf { // derivedStateOf to ensure that likesCount is recomputed
                                     // whenever likedSuggestions changes.
    if (isLiked) {
      // If the suggestion is liked, add one to the count of userLikes
      suggestion.userLikes.size + 1
    } else {
      // Otherwise, take the original count
      suggestion.userLikes.size
    }
  }

  // Define card colors with a white background
  val cardColors =
      CardDefaults.cardColors(
          containerColor = Color.White // This sets the background color of the Card
          )

  // Use Card for elevation and surface coloring, if needed
  Card(
      modifier =
          modifier
              .padding(8.dp)
              .width(380.dp) // the width of the Card
              .height(166.dp) // the height of the Card
              .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(25.dp))
              .clickable(
                  onClick = onClick), // Invoke the onClick lambda when the item is clicked (see
      // SuggestionFeedContent.kt)
      colors = cardColors // Use the cardColors with the white background
      ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
          Row( // Row for title and date
              modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = suggestion.stop.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = suggestion.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray)
              }

          Spacer(
              modifier =
                  Modifier.height(4.dp)) // Add spacing between the first row and the second row

          // the second row
          Text(
              text = suggestion.userName,
              style = MaterialTheme.typography.bodyMedium,
              fontSize = 14.sp)

          Spacer(
              modifier =
                  Modifier.height(8.dp)) // Add spacing between the second row and the third row

          // the third row
          Text(
              text = suggestion.stop.description,
              style = MaterialTheme.typography.bodySmall,
              fontSize = 14.sp,
              maxLines = 2, // Limit the text to two lines
              overflow = TextOverflow.Ellipsis // Add ellipsis if the text is longer than two lines
              )

          Spacer(modifier = Modifier.weight(1f)) // Pushes the icons to the bottom

          Row( // Row for comments and likes
              modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(240.dp)) // Use the space to align the mail icon

                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = null, // Decorative element
                    modifier = Modifier.size(18.dp))

                Spacer(modifier = Modifier.width(4.dp)) // Space between icon and text

                Text(
                    text = "${suggestion.comments.size}",
                )

                Spacer(modifier = Modifier.weight(1f)) // Pushes the heart icon to the end

                // Icon click handler
                Icon(
                    imageVector =
                        if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    modifier =
                        Modifier.size(18.dp).clickable {
                          viewModel.toggleLikeSuggestion(tripId, suggestion)
                        })

                Spacer(modifier = Modifier.width(4.dp)) // Space between icon and text

                Text(text = "$likesCount")
              }
        }
      }
}
