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
fun CommentBottomSheet(viewModel: SuggestionsViewModel, suggestion: Suggestion) {

  val bottomSheetVisible by viewModel.bottomSheetVisible.collectAsState()
  val selectedComment by viewModel.selectedComment.collectAsState()
  val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()

  val modalBottomSheetState = rememberModalBottomSheetState()

  if (selectedComment != null && bottomSheetVisible) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.hideBottomSheet() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("commentBottomSheet")) {
          // Add a list of options to be displayed in the bottom sheet
          Column(modifier = Modifier.navigationBarsPadding()) {
            // Only displays the option if the user is Admin or it is his comment
            if (SessionManager.canRemove(selectedComment!!.userId)) {
              Column {
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable(onClick = { viewModel.showDeleteDialog() })
                            .padding(16.dp)
                            .testTag("deleteCommentOption"),
                    contentAlignment = Alignment.CenterStart) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp)) // Space between icon and text
                        Text("Delete comment", style = MaterialTheme.typography.bodyLarge)
                      }
                    }

                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable(onClick = { viewModel.editCommentOption() })
                            .padding(16.dp)
                            .testTag("editCommentOption"),
                    contentAlignment = Alignment.CenterStart) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Create,
                            contentDescription = "Edit",
                            modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp)) // Space between icon and text
                        Text("Edit comment", style = MaterialTheme.typography.bodyLarge)
                      }
                    }
              }
            }
          }
        }
  }
  if (showDeleteDialog) {
    AlertDialog(
        onDismissRequest = { viewModel.hideDeleteDialog() },
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this comment?") },
        confirmButton = {
          TextButton(
              onClick = { viewModel.confirmDeleteComment(suggestion) },
              modifier = Modifier.testTag("confirmDeleteCommentButton")) {
                Text("Confirm")
              }
        },
        dismissButton = {
          TextButton(
              onClick = { viewModel.hideDeleteDialog() },
              modifier = Modifier.testTag("cancelDeleteCommentButton")) {
                Text("Cancel")
              }
        },
        modifier = Modifier.testTag("deleteCommentDialog"))
  }
}
