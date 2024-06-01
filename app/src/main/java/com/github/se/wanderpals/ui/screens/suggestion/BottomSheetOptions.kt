package com.github.se.wanderpals.ui.screens.suggestion
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.se.wanderpals.service.SessionManager

@Composable
fun BottomSheetOptions(
    canRemove: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onTransform: () -> Unit,
    deleteTestTag: String,
    editTestTag: String,
    transformTestTag: String
) {
    if (canRemove) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            OptionItem(
                icon = Icons.Outlined.Delete,
                text = "Delete",
                enabled = true,
                onClick = onDelete,
                testTag = deleteTestTag
            )
            OptionItem(
                icon = Icons.Outlined.Create,
                text = "Edit",
                enabled = SessionManager.getIsNetworkAvailable(),
                onClick = onEdit,
                testTag = editTestTag
            )
            OptionItem(
                icon = Icons.Outlined.Add,
                text = "Transform to a stop",
                enabled = SessionManager.getIsNetworkAvailable(),
                onClick = onTransform,
                testTag = transformTestTag
            )
        }
    }
}