package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/** The Map screen. */
@Composable
fun Map(tripId: String) {
  Text(modifier = Modifier.testTag("mapScreen"), text = "Map screen for tripId $tripId")
}
