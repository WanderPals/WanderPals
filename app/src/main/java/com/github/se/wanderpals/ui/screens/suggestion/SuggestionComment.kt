package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import java.time.format.DateTimeFormatter

/**
 * Composable function to display a suggestion comment.
 *
 * @param comment The comment to be displayed.
 * @param suggestionsViewModel The view model to handle the interactions with the suggestions.
 */
@Composable
fun SuggestionComment(comment: Comment, suggestionsViewModel: SuggestionsViewModel) {
  Column(
      modifier =
          Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
              .padding(16.dp)
              .fillMaxWidth()
              .testTag(comment.commentId)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
              text = comment.userName,
              style = TextStyle(fontWeight = FontWeight.Bold),
              modifier = Modifier.testTag("commentUserName" + comment.commentId))
          // Add spacer to push the icon to the far right
          Spacer(modifier = Modifier.weight(1f))
          // Three-dot icon
          Icon(
              imageVector = Icons.Outlined.MoreVert,
              contentDescription = "Options",
              modifier =
                  Modifier.clickable { suggestionsViewModel.showBottomSheet(comment) }
                      .testTag("commentOptionsIcon" + comment.commentId)
                      .padding(8.dp))
        }

        Text(
            text =
                "Created on : ${comment.createdAt.format(DateTimeFormatter.ofPattern("d MMM yyyy"))}",
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.testTag("commentCreatedAt" + comment.commentId))
        HorizontalDivider(
            modifier =
                Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    .testTag("commentDivider" + comment.commentId))

        Surface(
            modifier =
                Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)).padding(8.dp)) {
              Text(
                  text = comment.text,
                  modifier =
                      Modifier.padding(vertical = 4.dp)
                          .fillMaxWidth()
                          .testTag("commentText" + comment.commentId),
                  fontSize = 14.sp)
            }
      }
}
