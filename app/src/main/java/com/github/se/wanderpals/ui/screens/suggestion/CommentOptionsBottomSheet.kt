package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(onDismiss: () -> Unit) {
  val modalBottomSheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
      onDismissRequest = { onDismiss() },
      sheetState = modalBottomSheetState,
      dragHandle = { BottomSheetDefaults.DragHandle() },
  ) {
    // Add an option "delete comment"
    // Add an option "edit comment"
  }
}
