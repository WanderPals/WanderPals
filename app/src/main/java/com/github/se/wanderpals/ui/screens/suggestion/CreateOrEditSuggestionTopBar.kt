package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion

/**
 * Composable function for displaying the top bar in the Create Suggestion screen.
 *
 * @param suggestion The suggestion object for which the user is creating or editing a new suggestion.
 * @param onCancel Callback function for handling the back button click.
 */
@Composable
fun CreateOrEditSuggestionTopBar(
    suggestion: Suggestion,
    onCancel: () -> Unit) {
    Column(
        modifier = Modifier.testTag("createEditSuggestionTopBar"), //todo: create test for this
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier =
            Modifier
                .background(MaterialTheme.colorScheme.surfaceTint)
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { onCancel() },
                    modifier = Modifier.testTag("goBackButton")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text(
                    text =
                    if (suggestion.suggestionId.isEmpty()) "Create a new suggestion"
                    else "Edit the suggestion",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}