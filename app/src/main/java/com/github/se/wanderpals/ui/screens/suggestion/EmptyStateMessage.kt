package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.service.SessionManager

/**
 * Composable function to display a message when the list is empty
 *
 * @param message The message to display
 * @param onRefresh The action to perform when the refresh button is clicked
 * @param testTag The test tag for the message
 * @param contentDescription The content description for the refresh button
 * @param color The color of the message
 */
@Composable
fun EmptyStateMessage(
    message: String,
    onRefresh: () -> Unit,
    testTag: String,
    contentDescription: String,
    color: Color
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Text(
        modifier = Modifier.width(260.dp).height(55.dp).align(Alignment.Center).testTag(testTag),
        text = message,
        style =
            TextStyle(
                lineHeight = 20.sp,
                letterSpacing = 0.5.sp,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                color = color))
    IconButton(
        enabled = SessionManager.getIsNetworkAvailable(),
        onClick = onRefresh,
        modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
        content = { Icon(Icons.Default.Refresh, contentDescription = contentDescription) })
  }
}
