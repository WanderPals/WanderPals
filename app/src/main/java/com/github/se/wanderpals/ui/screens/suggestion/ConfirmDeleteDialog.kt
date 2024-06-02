package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.wanderpals.service.SessionManager

/**
 * A dialog that asks the user to confirm deletion of an item.
 *
 * @param onDismissRequest The callback to be invoked when the dialog is dismissed.
 * @param onConfirm The callback to be invoked when the user confirms the deletion.
 * @param modifier The modifier to be applied to the dialog.
 * @param confirmTestTag The test tag for the confirm button.
 * @param cancelTestTag The test tag for the cancel button.
 * @param dialogTestTag The test tag for the dialog.
 */
@Composable
fun ConfirmDeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmTestTag: String,
    cancelTestTag: String,
    dialogTestTag: String
) {
  AlertDialog(
      onDismissRequest = onDismissRequest,
      title = { Text("Confirm Deletion") },
      text = {
        Text(
            when (SessionManager.getIsNetworkAvailable()) {
              true -> "Are you sure you want to delete this item?"
              false -> "You are offline. You can't delete this item."
            })
      },
      confirmButton = {
        TextButton(onClick = onConfirm, modifier = modifier.testTag(confirmTestTag)) {
          Text("Confirm", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismissRequest, modifier = modifier.testTag(cancelTestTag)) {
          Text("Cancel")
        }
      },
      modifier = modifier.testTag(dialogTestTag))
}
