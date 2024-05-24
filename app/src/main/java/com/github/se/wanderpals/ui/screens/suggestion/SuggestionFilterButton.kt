package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuggestionFilterButton(text: String, isSelected: Boolean, onSelect: () -> Unit) {
  val backgroundColor =
      if (isSelected) MaterialTheme.colorScheme.inversePrimary
      else MaterialTheme.colorScheme.surfaceVariant
  val textColor = MaterialTheme.colorScheme.onSurface

  Text(
      text = text,
      style =
          TextStyle(
              lineHeight = 40.sp,
              fontWeight = FontWeight(500),
              textAlign = TextAlign.Center,
              color = textColor,
              letterSpacing = 0.5.sp,
          ),
      modifier =
          Modifier.height(24.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable(onClick = onSelect)
              .background(backgroundColor)
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("suggestionSortingButton"),
  )
}
