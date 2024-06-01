package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Composable function that displays the details of a suggestion. It shows the title, description,
 * address, website, schedule, and comments of the suggestion. The user can like the suggestion, add
 * a comment, and view the comments.
 *
 * @param viewModel The view model to get the suggestion details from.
 * @param navActions The navigation actions to navigate back.
 */
@Composable
fun SuggestionDetail(
    viewModel: SuggestionsViewModel,
    navActions: NavigationActions
) {

  val suggestionFromViewModel by viewModel.selectedSuggestion.collectAsState()
  val suggestion = suggestionFromViewModel

  if (suggestion != null) {
    // Get the number of likes and if the user has liked the suggestion
    val isLiked = viewModel.getIsLiked(suggestion.suggestionId)
    val likesCount = viewModel.getNbrLiked(suggestion.suggestionId)

    // Get a reference to the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() } // For managing focus
    var submitComment by remember { mutableStateOf(false) }

    var newCommentText by remember { mutableStateOf("") }
    val editingComment by viewModel.editingComment.collectAsState()
    val selectedComment by viewModel.selectedComment.collectAsState()

    Scaffold(
        topBar = {
            GoBackSuggestionTopBar(title = suggestion.stop.title, onBack = { navActions.goBack() })
        }) { paddingValues ->
          Column(
              modifier =
              Modifier
                  .padding(paddingValues)
                  .padding(horizontal = 12.dp)
                  .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Display which user the suggestion was created by and at which time
                Text(
                    text =
                        "Suggested by ${suggestion.userName} on ${suggestion.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("CreatedByText"))
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("DescriptionTitle"))
                Text(
                    text = suggestion.stop.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("DescriptionText"))

                // Additional details like address and website
              // Add a pin icon before the address
              DetailRow(
                  icon = Icons.Filled.LocationOn,
                  contentDescription = "Location",
                  text = "Address: ${suggestion.stop.address.ifBlank { "No address provided" }}",
                  color = if (suggestion.stop.address.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray,
                  testTag = "AddressText"
              )

                // Wrap a row around it and add a web icon like the address
              DetailRow(
                  icon = Icons.Filled.Info,
                  contentDescription = "Website",
                  text = "Website: ${suggestion.stop.website.ifBlank { "No website provided" }}",
                  color = if (suggestion.stop.website.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray,
                  testTag = "WebsiteText"
              )

                // Add row with an icon of an agenda and a text saying from when to when is the
                // suggestion scheduled
              val suggestionStartTime =
                  LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
              val suggestionEndTime =
                  suggestionStartTime.plusMinutes(suggestion.stop.duration.toLong())
              // text to display the schedule of the suggestion like "From 09/10/2024 at
              // 12:00
              // to 11/10/2024 at 14:00"
              DetailRow(
                  icon = Icons.Filled.DateRange,
                  contentDescription = "Schedule",
                  text = "From ${suggestionStartTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm"))} to ${suggestionEndTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm"))}",
                  color = MaterialTheme.colorScheme.onSurface,
                  testTag = "ScheduleText"
              )

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                      "Comments",
                      style = MaterialTheme.typography.titleMedium,
                      modifier = Modifier.testTag("CommentsHeader"))

                  Spacer(modifier = Modifier.weight(1f))

                  // Adding the Up icon for likes/votes
                  Icon(
                      painter =
                          if (isLiked) painterResource(R.drawable.up_filled)
                          else painterResource(R.drawable.up_outlined),
                      contentDescription = "Up",
                      tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.scrim,
                      modifier =
                      Modifier
                          .size(25.dp)
                          .padding(bottom = 1.dp, end = 4.dp)
                          .clickable { viewModel.toggleLikeSuggestion(suggestion) }
                          .testTag("upIcon"))
                  Text(
                      text = "$likesCount",
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.testTag("LikesCount"))

                  // Add a comment icon
                  Icon(
                      painter = painterResource(R.drawable.comment),
                      contentDescription = "Comment",
                      tint = MaterialTheme.colorScheme.scrim,
                      modifier =
                      Modifier
                          .size(33.dp)
                          .padding(start = 6.dp, end = 8.dp)
                          .testTag("CommentButton"))

                  // Add a text that shows the number of comments
                  Text(
                      text = suggestion.comments.size.toString(),
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.testTag("CommentsCount"))
                }

                // Listen for submitComment changes to dismiss keyboard and clear focus
                LaunchedEffect(submitComment) {
                  if (submitComment) {
                    keyboardController?.hide()
                    focusRequester.freeFocus()
                    submitComment = false // Reset the trigger
                  }
                }

                // Add a text field for adding a new comment
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    label = {
                      if (editingComment) Text("Modify your comment") else Text("Add a comment")
                    },
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .testTag("NewCommentInput")
                        .focusRequester(focusRequester)
                        .focusable(true), // Ensure it’s still focusable for user interaction
                    trailingIcon = {
                      IconButton(
                          onClick = {
                            onDone(
                                cond = editingComment,
                                string = newCommentText,
                                viewModel = viewModel,
                                suggestion = suggestion,
                                selectedComment = selectedComment,
                                exec = {
                                  newCommentText = ""
                                  submitComment = true
                                })
                          },
                          modifier = Modifier.testTag("SendButton")) {
                            if (!editingComment) {
                              Icon(
                                  Icons.AutoMirrored.Outlined.Send,
                                  contentDescription =
                                      "Send") // Change the icon to a send icon if adding a comment
                            } else {
                              if (newCommentText.isNotBlank()) {
                                Icon(
                                    Icons.Outlined.Create,
                                    contentDescription =
                                        "Edit") // Change the icon to an edit icon if editing a
                                // comment
                              } else {
                                Icon(
                                    Icons.Outlined.Clear,
                                    contentDescription = "Cancel",
                                ) // Change the icon to a clear icon if editing a comment and the
                                // text is empty
                              }
                            }
                          }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                              onDone(
                                  cond = editingComment,
                                  string = newCommentText,
                                  viewModel = viewModel,
                                  suggestion = suggestion,
                                  selectedComment = selectedComment,
                                  exec = {
                                    newCommentText = ""
                                    submitComment = true
                                  })
                            }))

                // Display the comments
                if (suggestion.comments.isNotEmpty()) {
                  suggestion.comments
                      .sortedByDescending { it.createdAt }
                      .forEach { comment ->
                        SuggestionComment(comment = comment, suggestionsViewModel = viewModel)
                      }
                } else {
                  Text(
                      "No comments yet",
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.testTag("NoCommentsMessage"))
                }
              }
          // Bottom sheet for comment options
          CommentBottomSheet(
              viewModel = viewModel,
              suggestion = suggestion,
              onEdit = {
                newCommentText = it
                focusRequester.requestFocus()
              }) // Pass the edit function to the bottom sheet
    }
  }
}

// Function to handle remove duplication of code for adding and editing comments
private fun onDone(
    cond: Boolean,
    string: String,
    viewModel: SuggestionsViewModel,
    suggestion: Suggestion,
    selectedComment: Comment?,
    exec: () -> Unit
) {
  if (cond) {
    if (string.isNotBlank()) {
      viewModel.updateComment(suggestion, selectedComment!!.copy(text = string))
    } else {
      viewModel.cancelEditComment()
    }
    exec()
  } else {
    if (string.isNotBlank()) {
      viewModel.addComment(
          suggestion, Comment("", "", "", string, LocalDate.now(), LocalTime.now()))
      exec()
    }
  }
}

/**
 * DetailRow composable function to display the details of a suggestion.
 *
 * @param icon The icon to display.
 * @param contentDescription The content description of the icon.
 * @param text The text to display.
 * @param color The color of the text.
 * @param testTag The test tag of the text.
 */
@Composable
fun DetailRow(icon: ImageVector, contentDescription: String, text: String, color: Color, testTag: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier
                .size(24.dp)
                .testTag(contentDescription + "Icon")
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.testTag(testTag)
        )
    }
}
