package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/** The Dashboard screen. */
@Composable
fun Dashboard() {
  Text(modifier = Modifier.testTag("dashboardScreen"), text = "Dashboard")
}
