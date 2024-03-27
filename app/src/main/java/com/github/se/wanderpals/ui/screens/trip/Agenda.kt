package com.github.se.wanderpals.ui.screens.trip

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/** The Agenda screen. */
@Composable
fun Agenda() {
  Text(modifier = Modifier.testTag("agendaScreen"), text = "Agenda")
}
