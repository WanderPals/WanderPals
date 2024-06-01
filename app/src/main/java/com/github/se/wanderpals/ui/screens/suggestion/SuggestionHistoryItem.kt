package com.github.se.wanderpals.ui.screens.suggestion

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Composable function that represents a single suggestion item in the suggestion feed.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param modifier The modifier to be applied to the suggestion item.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun SuggestionHistoryItem(
    suggestion: Suggestion,
    viewModel: SuggestionsViewModel,
    modifier: Modifier = Modifier
) {
  val isLiked = viewModel.getIsLiked(suggestion.suggestionId)
  val likesCount = viewModel.getNbrLiked(suggestion.suggestionId).toString()
  val cardColors =
      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

  ElevatedCard(
      modifier =
      modifier
          .padding(
              start = 27.dp,
              end = 27.dp,
              top = 12.dp,
              bottom = 12.dp
          ) // the padding between the screen and the suggestionItem
          .fillMaxWidth()
          .height(166.dp)
          .testTag("suggestionHistory"),
      colors = cardColors,
      shape = RoundedCornerShape(6.dp),
      elevation = CardDefaults.cardElevation(10.dp),
  ) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        // Header
        SuggestionHeader(suggestion = suggestion, testTagPrefix = "suggestionHistory")
      Spacer(modifier = Modifier.height(8.dp))

      // Description
        SuggestionDescription(suggestion, "suggestionHistoryDescription")

      Spacer(modifier = Modifier.height(12.dp))

      // User and Icons
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = "Suggested by ${suggestion.userName}",
            style =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.tertiary,
                    letterSpacing = 0.14.sp,
                ),
            modifier = Modifier.testTag("suggestionHistoryUserName" + suggestion.suggestionId))

        Spacer(Modifier.weight(1f)) // push the icons to the right

        Row {
          Icon(
              painter =
                  if (isLiked) painterResource(R.drawable.up_filled)
                  else painterResource(R.drawable.up_outlined),
              contentDescription = "Up",
              tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.tertiary,
              modifier =
              Modifier
                  .size(20.dp)
                  .padding(
                      bottom = 4.dp,
                      end = 4.dp
                  ) // 4.dp is the space between the icon and the text
                  .testTag(
                      "staticUpIconSuggestionHistoryFeedScreen_${suggestion.suggestionId}"
                  ))

          Text(
              text = likesCount,
              style =
                  TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 20.sp,
                      fontWeight = FontWeight(500),
                      color = MaterialTheme.colorScheme.tertiary,
                      letterSpacing = 0.14.sp,
                  ),
              modifier = Modifier.testTag("suggestionHistoryUpsNumber" + suggestion.suggestionId))

          Spacer(
              modifier =
                  Modifier.width(8.dp)) // 8.dp is the space between the text and the next icon

          Icon(
              painter = painterResource(R.drawable.comment),
              contentDescription = "Comment",
              tint = MaterialTheme.colorScheme.tertiary,
              modifier =
              Modifier
                  .size(20.dp)
                  .padding(
                      bottom = 2.dp,
                      end = 4.dp
                  ) // 4.dp is the space between the texts and the icon
                  .testTag(
                      "staticCommentIconSuggestionHistoryFeedScreen" + suggestion.suggestionId
                  ))

          Text(
              text = "${suggestion.comments.size}",
              style =
                  TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 20.sp,
                      fontWeight = FontWeight(500),
                      color = MaterialTheme.colorScheme.tertiary,
                      letterSpacing = 0.14.sp,
                  ),
              modifier =
                  Modifier.testTag("suggestionHistoryCommentsNumber" + suggestion.suggestionId))
        }
      }
    }
  }
}
