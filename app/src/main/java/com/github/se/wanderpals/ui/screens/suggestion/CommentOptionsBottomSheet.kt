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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(viewModel: SuggestionsViewModel) {

  val bottomSheetVisible by viewModel.bottomSheetVisible.collectAsState()
  val selectedComment by viewModel.selectedComment.collectAsState()

  val modalBottomSheetState = rememberModalBottomSheetState()

  if (selectedComment != null && bottomSheetVisible) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.hideBottomSheet() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
      // Add a list of options to be displayed in the bottom sheet
      Column(
          modifier = Modifier.navigationBarsPadding()
      ) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .clickable(onClick = { viewModel.deleteComment() })
                    .padding(16.dp),
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
      }
    }
  }
}
