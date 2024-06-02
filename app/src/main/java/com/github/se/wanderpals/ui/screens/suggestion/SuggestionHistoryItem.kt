package com.github.se.wanderpals.ui.screens.suggestion

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel

/**
 * Composable function that represents a single suggestion history item in the suggestion history
 * feed screen.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param viewModel The ViewModel for managing suggestions.
 * @param modifier The modifier to be applied to the suggestion history item.
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
                  bottom = 12.dp) // the padding between the screen and the suggestionItem
              .fillMaxWidth()
              .height(166.dp)
              .testTag("suggestionHistory"),
      colors = cardColors,
      shape = RoundedCornerShape(6.dp),
      elevation = CardDefaults.cardElevation(10.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
      // Header
      SuggestionHeader(suggestion = suggestion, testTagPrefix = "suggestionHistory")
      Spacer(modifier = Modifier.height(8.dp))

      // Description
      SuggestionDescription(suggestion, "suggestionHistoryDescription")

      Spacer(modifier = Modifier.height(12.dp))

      // User and Icons
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        ItemText(
            text = "Suggested by ${suggestion.userName}",
            "suggestionHistoryUserName" + suggestion.suggestionId)

        Spacer(Modifier.weight(1f)) // push the icons to the right

        Row {
          Icon(
              painter =
                  if (isLiked) painterResource(R.drawable.up_filled)
                  else painterResource(R.drawable.up_outlined),
              contentDescription = "Up",
              tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.tertiary,
              modifier =
                  Modifier.size(20.dp)
                      .padding(
                          bottom = 4.dp,
                          end = 4.dp) // 4.dp is the space between the icon and the text
                      .testTag(
                          "staticUpIconSuggestionHistoryFeedScreen_${suggestion.suggestionId}"))

          ItemText(text = likesCount, "suggestionHistoryUpsNumber" + suggestion.suggestionId)

          Spacer(
              modifier =
                  Modifier.width(8.dp)) // 8.dp is the space between the text and the next icon

          VoteOrCommentIcon(
              painterResource = R.drawable.comment,
              contentDescription = "Comment",
              paddingValues = PaddingValues(bottom = 2.dp, end = 4.dp),
              testTag = "staticCommentIconSuggestionHistoryFeedScreen" + suggestion.suggestionId)

          ItemText(
              text = "${suggestion.comments.size}",
              "suggestionHistoryCommentsNumber" + suggestion.suggestionId)
        }
      }
    }
  }
}
