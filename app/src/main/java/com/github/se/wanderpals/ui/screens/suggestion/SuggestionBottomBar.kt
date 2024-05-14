package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R

@Composable
fun SuggestionBottomBar(
    onSuggestionClick: () -> Unit = {},
    onHistoryClick: () -> Unit // Add this parameter for navigation action
) {

  // Button to create a suggestion
  Box(Modifier.fillMaxWidth().padding(bottom = 16.dp)) { // Added padding to raise the button
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          Button(
              onClick = { onHistoryClick() },
              modifier =
                  Modifier.padding(start = 27.dp) // Add padding to the start of the screen
                      .height(64.dp) // Set the height to 56.dp to make it a square
                      .testTag("suggestionHistoryButtonExists"),
              shape = RoundedCornerShape(size = 16.dp),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer, // Button color
                      contentColor = MaterialTheme.colorScheme.onPrimaryContainer, // Icon color
                  )) {
                Icon(
                    painter = painterResource(R.drawable.history),
                    contentDescription = "History",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
              }

          Button(
              onClick = { onSuggestionClick() },
              modifier =
                  Modifier // .align(Alignment.BottomEnd) // Align the button to the bottom end of
                      // the screen
                      .padding(end = 27.dp) // Add padding to the end of the screen
                      .height(64.dp) // Set the height to 56.dp to make it a square
                      .testTag("suggestionButtonExists"),
              shape = RoundedCornerShape(size = 16.dp),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer, // Button color
                      contentColor = MaterialTheme.colorScheme.onPrimaryContainer, // Icon color
                  )) {
                Icon(
                    imageVector = Icons.Default.Add,
                    modifier = Modifier.size(24.dp),
                    contentDescription = null)
              }
        }
  }
}
