package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Composable function for displaying the top bar in any screen related to suggestions that requires
 * a back button and a title in the top bar.
 *
 * @param title The title of the screen.
 * @param onBack Callback function for handling the back button click.
 */
@Composable
fun GoBackSuggestionTopBar(title: String, onBack: () -> Unit) {
  Column(
      modifier = Modifier.testTag("goBackSuggestionTopBar"), // todo: create test for this
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.surfaceTint)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center) {
                    IconButton(
                        onClick = { onBack() }, modifier = Modifier.testTag("goBackButton")) {
                          Icon(
                              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                              contentDescription = "Back",
                              tint = MaterialTheme.colorScheme.onPrimary,
                          )
                        }
                    Text(
                        text = title,
                        modifier = Modifier.testTag("SuggestionTitle"),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary)
                  }
            }
        Spacer(modifier = Modifier.height(8.dp))
      }
}
