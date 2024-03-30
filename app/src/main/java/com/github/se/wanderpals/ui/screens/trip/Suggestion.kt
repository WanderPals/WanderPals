package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/** The Finance screen. */
@Composable
fun Suggestion(tripId: String) {
  Text(modifier = Modifier.testTag("suggestionScreen"), text = "Suggestion for trip $tripId")
}
