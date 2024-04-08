package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.Suggestion
import java.time.format.DateTimeFormatter

@Composable
fun SuggestionDetailPopup(
    suggestion: Suggestion,
    comments: List<Comment>, // Assuming you have a list of comments
    onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    // The semi-transparent overlay will be provided by the Dialog itself
    Surface(
        modifier = Modifier.testTag("suggestionPopupScreen"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface) {
          Column(
              modifier =
                  Modifier.width(360.dp)
                      .padding(16.dp)
                      .verticalScroll(rememberScrollState()) // Enable vertical scrolling
              ) {
                // Suggestion Title and Close button
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      // Likes and Comments Count
                      Row {
                        Text(
                            text = suggestion.stop.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            //                            modifier = Modifier.weight(1f)
                            modifier =
                                Modifier.weight(1f)
                                    .wrapContentWidth(
                                        Alignment
                                            .Start) // Aligns text to the start, ensuring it doesn't
                                                    // stretch the row width
                                    .testTag("suggestionPopupTitle"))

                        Spacer(modifier = Modifier.width(8.dp)) // Fixed spacing after the title

                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Comments",
                            modifier = Modifier.size(18.dp).testTag("suggestionPopupCommentsIcon"))
                        Text(text = "${suggestion.comments.size}")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Likes",
                            modifier = Modifier.size(18.dp).testTag("suggestionPopupLikesIcon"))
                        Text(
                            text = "${suggestion.userLikes.size}",
                            modifier = Modifier.padding(end = 8.dp))
                      }
                    }

                // Username and creation date
                /*Text(
                    text = "${suggestion.userName}, on ${suggestion.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium
                )*/

                Row {
                  Text(
                      text = suggestion.userName,
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.testTag("suggestionPopupUserName"))

                  Text(text = ", on ", style = MaterialTheme.typography.bodyMedium)

                  Text(
                      text = suggestion.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.testTag("suggestionPopupDate"))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier.padding(bottom = 8.dp).testTag("suggestionPopupDescription"))
                // Suggestion Text
                Text(
                    text = suggestion.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier =
                        Modifier.padding(bottom = 24.dp).testTag("suggestionPopupDescriptionText"))

                Text(
                    text = "Comments",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp).testTag("suggestionPopupComments"))
                if (comments.isEmpty()) {
                  Text(
                      text = "No comments yet",
                      style = MaterialTheme.typography.bodyMedium,
                      color = Color.Gray,
                      modifier =
                          Modifier.padding(start = 8.dp, bottom = 8.dp)
                              .testTag("noSuggestionCommentList"))
                } else {
                  // List of Comments
                  comments.forEach { comment ->
                    Text(
                        text = comment.text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier.testTag("suggestionComment${comments.indexOf(comment) + 1}"))
                    if (comments.indexOf(comment) != comments.size - 1) {
                      Divider(
                          color = Color(0xFF5A7BF0),
                          modifier =
                              Modifier.testTag(
                                  "suggestionPopupDivider")) // separate comments with a line if
                                                             // it's not the last comment
                    }
                  }
                }
              }
        }
  }
}
