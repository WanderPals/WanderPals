package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
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
    isLiked: Boolean, // a boolean to track if the user has liked the suggestion
    likesCount: Int, // the number of likes for the suggestion
    onDismiss: () -> Unit, // Callback to dismiss the dialog
    onLikeClicked: () -> Unit // Callback to handle like button click
) {
  Dialog(
      onDismissRequest =
          onDismiss) { // todo: (after M1) uncomment dialog and create onclick function to go from
                       // suggestionItem to the page SuggestionDetailPopup
        // The semi-transparent overlay will be provided by the Dialog itself
        BoxWithConstraints {
          val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() * 0.8f }
            var newCommentText by remember { mutableStateOf("") }
          Surface(
              modifier = Modifier
                  .heightIn(max = maxHeight)
                  .testTag("suggestionPopupScreen"),
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surface) {
                Column(
                    modifier =
                    Modifier
                        .width(360.dp)
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
                                  Modifier
                                      .weight(1f)
                                      .wrapContentWidth(
                                          Alignment
                                              .Start
                                      ) // Aligns text to the start, ensuring it
                                      // doesn't
                                      // stretch the row width
                                      .testTag("suggestionPopupTitle"))

                              Spacer(
                                  modifier = Modifier.width(8.dp)) // Fixed spacing after the title

                              Icon(
                                  imageVector = Icons.Default.MailOutline,
                                  contentDescription = "Comments",
                                  modifier =
                                  Modifier
                                      .size(18.dp)
                                      .testTag("suggestionPopupCommentsIcon"))
                              Text(text = "${suggestion.comments.size}")

                              Spacer(modifier = Modifier.width(4.dp))

                              Icon(
                                  imageVector =
                                      if (isLiked) Icons.Filled.Favorite
                                      else Icons.Default.FavoriteBorder,
                                  contentDescription = "Likes",
                                  modifier =
                                  Modifier
                                      .size(18.dp)
                                      .clickable(
                                          onClick = onLikeClicked
                                      ) // make the icon clickable
                                      .testTag("suggestionPopupLikesIcon"))
                              Text(text = "$likesCount", modifier = Modifier.padding(end = 8.dp))
                            }
                          }

                      // Username and creation date
                      Row {
                        Text(
                            text = suggestion.userName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("suggestionPopupUserName"))

                        Text(text = ", created: ", style = MaterialTheme.typography.bodyMedium)

                        Text(
                            text =
                                suggestion.createdAt.format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("suggestionPopupDate"))
                      }

                      Spacer(modifier = Modifier.height(16.dp))

                      Text(
                          text = "Description",
                          style = MaterialTheme.typography.bodyLarge,
                          fontWeight = FontWeight.Bold,
                          modifier =
                          Modifier
                              .padding(bottom = 8.dp)
                              .testTag("suggestionPopupDescription"))
                      // Suggestion Text
                      Text(
                          text = suggestion.text,
                          style = MaterialTheme.typography.bodyLarge,
                          modifier =
                          Modifier
                              .padding(bottom = 8.dp)
                              .testTag("suggestionPopupDescriptionText"))

                      // Calculate the end time and potentially the next day
                      val endTime =
                          suggestion.stop.startTime.plusMinutes(suggestion.stop.duration.toLong())
                      var endDate = suggestion.stop.date
                      if (endTime.isBefore(suggestion.stop.startTime)) {
                        endDate =
                            endDate.plusDays(
                                1) // Add a day if the endTime is before startTime due to overflow
                      }

                      // Display Date and Time Information
                      Text(
                          text =
                              "Scheduled from ${suggestion.stop.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                                  "${suggestion.stop.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                                  "to ${endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} ${
                              endTime.format(
                                  DateTimeFormatter.ofPattern("HH:mm")
                              )
                          }",
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.SemiBold,
                          modifier =
                          Modifier
                              .padding(bottom = 8.dp)
                              .testTag("suggestionPopupStartDateTimeEndDateTime"))

                      // Display Address and Website if available
                      if (suggestion.stop.address.isNotEmpty()) {
                        Text(
                            text = "Address: ${suggestion.stop.address}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier =
                            Modifier
                                .padding(top = 8.dp, bottom = 4.dp)
                                .testTag("suggestionPopupAddrTextNotEmpty"))
                      } else {
                        Row(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                          Text(
                              text = "Address: ",
                              style = MaterialTheme.typography.bodyMedium,
                              fontWeight = FontWeight.SemiBold,
                              modifier = Modifier.testTag("suggestionPopupAddr"))
                          Text(
                              text = "No address provided",
                              style = MaterialTheme.typography.bodyMedium,
                              color = Color.Gray,
                              modifier = Modifier.testTag("suggestionPopupAddrTextEmpty"))
                        }
                      }

                      if (suggestion.stop.website.isNotEmpty()) {
                        Text(
                            text = "Website: ${suggestion.stop.website}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier =
                            Modifier
                                .padding(bottom = 8.dp)
                                .testTag("suggestionPopupWebsiteTextNotEmpty"))
                      } else {
                        Row(modifier = Modifier.padding(bottom = 24.dp)) {
                          Text(
                              text = "Website: ",
                              style = MaterialTheme.typography.bodyMedium,
                              fontWeight = FontWeight.SemiBold,
                              modifier = Modifier.testTag("suggestionPopupWebsite"))
                          Text(
                              text = "No website provided",
                              style = MaterialTheme.typography.bodyMedium,
                              color = Color.Gray,
                              modifier = Modifier.testTag("suggestionPopupWebsiteTextEmpty"))
                        }
                      }

                      // Display Comments
                      Text(
                          text = "Comments",
                          style = MaterialTheme.typography.bodyMedium,
                          fontWeight = FontWeight.Bold,
                          modifier =
                          Modifier
                              .padding(bottom = 8.dp)
                              .testTag("suggestionPopupComments"))

                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Add a comment") },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .height(60.dp)
                            .verticalScroll(rememberScrollState(), enabled = true)
                            .testTag("suggestionPopupCommentTextField"),
                        textStyle = TextStyle(fontSize = 14.sp)
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .testTag(
                                "suggestionPopupDivider"
                            ), color = Color(0xFF5A7BF0))

                      if (comments.isEmpty()) {
                        Text(
                            text = "No comments yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier =
                            Modifier
                                .padding(start = 8.dp, bottom = 8.dp)
                                .testTag("noSuggestionCommentList"))
                      } else {
                        // List of Comments
                        comments.sortedByDescending { it.createdAt }.forEach { comment ->
                          SuggestionComment(comment = comment)
                          if (comments.indexOf(comment) != comments.size - 1) {
                              HorizontalDivider(
                                  modifier = Modifier
                                      .padding(vertical = 8.dp)
                                      .testTag(
                                          "suggestionPopupDivider${comments.indexOf(comment)}"
                                      ), color = Color(0xFF5A7BF0)
                              ) // Add a divider between comments except for the last one
                          }
                        }
                      }
                    }
              }
        }
      }
}
