package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.service.SessionManager

/**
 * Composable function to display the bottom sheet with options for a comment.
 *
 * @param viewModel The view model to handle the interactions with the suggestions.
 * @param suggestion The suggestion to which the comment belongs.
 * @param onEdit The function to call when the user wants to edit a comment.
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
        modifier = Modifier.testTag("commentBottomSheet")) {
          BottomSheetOptions(
              canRemove = SessionManager.canRemove(selectedComment!!.userId),
              onDelete = { viewModel.showDeleteDialog() },
              onEdit = {
                viewModel.editCommentOption()
                onEdit(selectedComment!!.text)
              },
              onTransform = {},
              deleteTestTag = "deleteCommentOption",
              editTestTag = "editCommentOption",
              transformTestTag = "" // Not used in this context
              )
        }
  }
  if (showDeleteDialog) {
    ConfirmDeleteDialog(
        onDismissRequest = { viewModel.hideDeleteDialog() },
        onConfirm = {
          if (SessionManager.getIsNetworkAvailable()) {
            viewModel.confirmDeleteComment(suggestion)
          } else {
            viewModel.hideDeleteDialog()
          }
        },
        confirmTestTag = "confirmDeleteCommentButton",
        cancelTestTag = "cancelDeleteCommentButton",
        dialogTestTag = "deleteCommentDialog")
  }
}
