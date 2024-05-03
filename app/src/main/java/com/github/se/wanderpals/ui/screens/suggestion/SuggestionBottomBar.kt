package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.ui.theme.onPrimaryContainerLight
import com.github.se.wanderpals.ui.theme.primaryContainerLight

@Composable
fun SuggestionBottomBar(onSuggestionClick: () -> Unit = {}) {
  Column(Modifier.padding(30.dp)) {

    // Button to create a suggestion
    Box(Modifier.fillMaxWidth()) {
      Button(
          onClick = { onSuggestionClick() },
          modifier =
              Modifier.padding(bottom = 20.dp)
                  .align(Alignment.TopCenter)
                  .width(300.dp)
                  .height(70.dp)
                  .testTag("suggestionButtonExists"),
          colors = ButtonDefaults.buttonColors(primaryContainerLight)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      imageVector = Icons.Default.Add,
                      tint = onPrimaryContainerLight,
                      modifier = Modifier.size(20.dp),
                      contentDescription = null)
                  Text(
                      text = "Create a suggestion",
                      style =
                          TextStyle(
                              lineHeight = 20.sp,
                              fontSize = 20.sp,
                              fontWeight = FontWeight(500),
                              textAlign = TextAlign.Center,
                              letterSpacing = 0.1.sp,
                              color = onPrimaryContainerLight))
                }
          }
    }
  }
}
