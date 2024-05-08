package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Comment
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Composable function that displays the details of a suggestion. It shows the title, description,
 * address, website, schedule, and comments of the suggestion. The user can like the suggestion, add
 * a comment, and view the comments.
 *
 * @param suggestionId The ID of the suggestion to display.
 * @param viewModel The view model to get the suggestion details from.
 * @param navActions The navigation actions to navigate back.
 * @see SuggestionsViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionDetail(
    suggestionId: String,
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
          TopAppBar(
              title = {
                Text(text = suggestion.stop.title, modifier = Modifier.testTag("SuggestionTitle"))
              },
              navigationIcon = {
                IconButton(
                    onClick = { navActions.goBack() }, modifier = Modifier.testTag("BackButton")) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Go back",
                      )
                    }
              },
              colors =
                  TopAppBarDefaults.topAppBarColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer,
                      titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer),
          )
        }) { paddingValues ->
          Column(
              modifier =
                  Modifier.padding(paddingValues)
                      .padding(horizontal = 12.dp)
                      .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                // Display which user the suggestion was created by and at which time
                Text(
                    text = "Suggested by ${suggestion.userName} on ${suggestion.createdAt}",
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                  // Add a pin icon before the address
                  Icon(
                      imageVector = Icons.Filled.LocationOn,
                      contentDescription = "Location",
                      tint =
                          if (suggestion.stop.address.isNotBlank())
                              MaterialTheme.colorScheme.onSurface
                          else Color.Gray,
                      modifier =
                          Modifier.testTag("LocationIcon")
                              // Change the size of the icon
                              .size(24.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                      text =
                          "Address: ${suggestion.stop.address.ifBlank { "No address provided" }}",
                      style = MaterialTheme.typography.bodyMedium,
                      color =
                          if (suggestion.stop.address.isNotBlank())
                              MaterialTheme.colorScheme.onSurface
                          else Color.Gray,
                      modifier = Modifier.testTag("AddressText"))
                }

                // Wrap a row around it and add a web icon like the address
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      imageVector = Icons.Filled.Info,
                      contentDescription = "Website",
                      tint =
                          if (suggestion.stop.website.isNotBlank())
                              MaterialTheme.colorScheme.onSurface
                          else Color.Gray,
                      modifier = Modifier.testTag("WebsiteIcon").size(24.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                      text =
                          "Website: ${suggestion.stop.website.ifBlank { "No website provided" }}",
                      style = MaterialTheme.typography.bodyMedium,
                      color =
                          if (suggestion.stop.website.isNotBlank())
                              MaterialTheme.colorScheme.onSurface
                          else Color.Gray,
                      modifier = Modifier.testTag("WebsiteText"))
                }

                // Add row with an icon of an agenda and a text saying from when to when is the
                // suggestion scheduled
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      imageVector = Icons.Filled.DateRange,
                      contentDescription = "Schedule",
                      tint = MaterialTheme.colorScheme.onSurface,
                      modifier = Modifier.testTag("ScheduleIcon").size(24.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                      // text to display the schedule of the suggestion like "From 09/10/2024 at
                      // 12:00
                      // to 11/10/2024 at 14:00"
                      text =
                          suggestion.stop.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                              " at ${suggestion.stop.startTime} " +
                              "to ${suggestion.stop.startTime.plusMinutes(suggestion.stop.duration.toLong())}",
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.testTag("ScheduleText"))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                      "Comments",
                      style = MaterialTheme.typography.titleMedium,
                      modifier = Modifier.testTag("CommentsHeader"))
                  Spacer(modifier = Modifier.weight(1f))
                  // Adding the heart icon for likes
                  IconButton(
                      onClick = { viewModel.toggleLikeSuggestion(suggestion) },
                      modifier = Modifier.testTag("LikeButton")) {
                        Icon(
                            imageVector =
                                if (isLiked) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else Color.Gray,
                        )
                      }
                  Text(
                      text = "$likesCount",
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.testTag("LikesCount"))
                  // Add a comment icon
                  IconButton(onClick = {}, modifier = Modifier.testTag("CommentButton")) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Comment",
                    )
                  }
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
                        Modifier.fillMaxWidth()
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
