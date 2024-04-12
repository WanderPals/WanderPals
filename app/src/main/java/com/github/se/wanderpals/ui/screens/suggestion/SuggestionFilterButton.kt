package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun SuggestionFilterButton(text: String, isSelected: Boolean, onSelect: () -> Unit) {
  val backgroundColor = if (isSelected) Color(0xFF5A7BF0) else Color(0xFFE1E2E1)
  val textColor = if (isSelected) Color.White else Color.Black

  Text(
      text = text,
      modifier =
          Modifier.clip(RoundedCornerShape(8.dp))
              .clickable(onClick = onSelect)
              .background(backgroundColor)
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("suggestionSortingButton"),
      color = textColor,
      style = MaterialTheme.typography.bodyMedium)
}
