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
 * Composable function to display the bottom sheet with options for a suggestion.
 *
 * @param viewModel The view model to handle the interactions with the suggestions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionBottomSheet(
    viewModel: SuggestionsViewModel,
    onEdit: (Suggestion) -> Unit = { _ -> }
) {

  val bottomSheetVisible by viewModel.bottomSheetVisible.collectAsState()
  val selectedSuggestion by viewModel.selectedSuggestion.collectAsState()
  val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
  val modalBottomSheetState = rememberModalBottomSheetState()

  if (selectedSuggestion != null && bottomSheetVisible) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.hideBottomSheet() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("suggestionBottomSheet")) {
          BottomSheetOptions(
              canRemove = SessionManager.canRemove(selectedSuggestion!!.userId),
              onDelete = { viewModel.showDeleteDialog() },
              onEdit = {
                onEdit(selectedSuggestion!!)
                viewModel.hideBottomSheet()
              },
              onTransform = { viewModel.transformToStop(selectedSuggestion!!) },
              deleteTestTag = "deleteSuggestionOption",
              editTestTag = "editSuggestionOption",
              transformTestTag = "transformSuggestionOption")
        }
  }
  if (showDeleteDialog) {
    ConfirmDeleteDialog(
        onDismissRequest = { viewModel.hideDeleteDialog() },
        onConfirm = {
          if (SessionManager.getIsNetworkAvailable()) {
            viewModel.confirmDeleteSuggestion(selectedSuggestion!!)
          } else {
            viewModel.hideDeleteDialog()
          }
        },
        confirmTestTag = "confirmDeleteSuggestionButton",
        cancelTestTag = "cancelDeleteSuggestionButton",
        dialogTestTag = "deleteSuggestionDialog")
  }
}
