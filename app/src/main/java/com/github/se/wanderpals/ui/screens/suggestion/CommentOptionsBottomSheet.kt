package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.service.SessionManager

/**
 * Composable function to display the bottom sheet with options for a comment.
 *
 * @param viewModel The view model to handle the interactions with the suggestions.
 * @param suggestion The suggestion to which the comment belongs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    viewModel: SuggestionsViewModel,
    suggestion: Suggestion,
    onEdit: (String) -> Unit = {}
) {

  val bottomSheetVisible by viewModel.bottomSheetVisible.collectAsState()
  val selectedComment by viewModel.selectedComment.collectAsState()
  val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
  val modalBottomSheetState = rememberModalBottomSheetState()

  if (selectedComment != null && bottomSheetVisible) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.hideBottomSheet() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("commentBottomSheet")
    ) {
        CommentOptions(viewModel, selectedComment!!.userId, selectedComment!!.text, onEdit)
    }
  }
  if (showDeleteDialog) {
    ConfirmDeleteDialog(viewModel, suggestion)
  }
}

/**
 * Composable function to display the options for a comment.
 *
 * @param viewModel The view model to handle the interactions with the suggestions.
 * @param commentUserId The user id of the comment.
 * @param commentText The text of the comment.
 * @param onEdit The callback function for editing a comment.
 */
@Composable
fun CommentOptions(
    viewModel: SuggestionsViewModel,
    commentUserId: String,
    commentText: String,
    onEdit: (String) -> Unit
) {
    Column(modifier = Modifier.navigationBarsPadding()) {
        if (SessionManager.canRemove(commentUserId)) {
            CommentOption(
                icon = Icons.Outlined.Delete,
                text = "Delete comment",
                enabled = true,
                onClick = { viewModel.showDeleteDialog() },
                testTag = "deleteCommentOption"
            )
            CommentOption(
                icon = Icons.Outlined.Create,
                text = "Edit comment",
                enabled = SessionManager.getIsNetworkAvailable(),
                onClick = {
                    viewModel.editCommentOption()
                    onEdit(commentText)
                },
                testTag = "editCommentOption"
            )
        }
    }
}

/**
 * Composable function to display an option for a comment.
 *
 * @param icon The icon for the option.
 * @param text The text for the option.
 * @param enabled Whether the option is enabled.
 * @param onClick The callback function for the option.
 * @param testTag The test tag for the option.
 */
@Composable
fun CommentOption(
    icon: ImageVector,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp)) // space between icon and text
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * Composable function to display a dialog to confirm the deletion of a comment.
 *
 * @param viewModel The view model to handle the interactions with the suggestions.
 * @param suggestion The suggestion to which the comment belongs.
 */
@Composable
fun ConfirmDeleteDialog(
    viewModel: SuggestionsViewModel,
    suggestion: Suggestion
) {
    AlertDialog(
        onDismissRequest = { viewModel.hideDeleteDialog() },
        title = { Text("Confirm Deletion") },
        text = {
            Text(
                when (SessionManager.getIsNetworkAvailable()) {
                    true -> "Are you sure you want to delete this comment?"
                    false -> "You are offline. You can't delete this comment."
                })
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when (SessionManager.getIsNetworkAvailable()) {
                        true -> viewModel.confirmDeleteComment(suggestion)
                        false -> viewModel.hideDeleteDialog()
                    }
                },
                modifier = Modifier.testTag("confirmDeleteCommentButton")
            ) {
                Text("Confirm", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.hideDeleteDialog() },
                modifier = Modifier.testTag("cancelDeleteCommentButton")) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("deleteCommentDialog")
    )
}