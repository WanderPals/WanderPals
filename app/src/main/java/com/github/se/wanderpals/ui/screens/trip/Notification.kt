package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/** The Notification screen. */
@Composable
fun Notification(tripId: String) {
  Text(modifier = Modifier.testTag("notificationScreen"), text = "Notification with tripId $tripId")
}
