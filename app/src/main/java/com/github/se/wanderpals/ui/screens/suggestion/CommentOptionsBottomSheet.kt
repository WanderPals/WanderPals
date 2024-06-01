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
//        CommentOptions(viewModel, selectedComment!!.userId, selectedComment!!.text, onEdit)
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
            dialogTestTag = "deleteCommentDialog"
      )
  }
}

///**
// * Composable function to display the options for a comment.
// *
// * @param viewModel The view model to handle the interactions with the suggestions.
// * @param commentUserId The user id of the comment.
// * @param commentText The text of the comment.
// * @param onEdit The callback function for editing a comment.
// */
//@Composable
//fun CommentOptions(
//    viewModel: SuggestionsViewModel,
//    commentUserId: String,
//    commentText: String,
//    onEdit: (String) -> Unit
//) {
//    Column(modifier = Modifier.navigationBarsPadding()) {
//        if (SessionManager.canRemove(commentUserId)) {
//            CommentOption(
//                icon = Icons.Outlined.Delete,
//                text = "Delete comment",
//                enabled = true,
//                onClick = { viewModel.showDeleteDialog() },
//                testTag = "deleteCommentOption"
//            )
//            CommentOption(
//                icon = Icons.Outlined.Create,
//                text = "Edit comment",
//                enabled = SessionManager.getIsNetworkAvailable(),
//                onClick = {
//                    viewModel.editCommentOption()
//                    onEdit(commentText)
//                },
//                testTag = "editCommentOption"
//            )
//        }
//    }
//}

