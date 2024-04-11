package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DashboardSuggestion(suggestion: Suggestion) {
  Card(modifier = Modifier.padding(8.dp).testTag("suggestion${(suggestion.suggestionId)}")) {
    Column(
        modifier =
            Modifier.border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp)).padding(16.dp)) {
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = suggestion.stop.title,
                    modifier = Modifier.testTag("stopTitle").weight(65f))
                Text(
                    text =
                        suggestion.createdAt.format(
                            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    modifier = Modifier.testTag("createdAt").weight(35f))
              }
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = suggestion.userName, modifier = Modifier.testTag("userName"))
                Text(
                    text =
                        "${suggestion.stop.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))} " +
                            "${suggestion.stop.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} -> \n" +
                            LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
                                .plusMinutes(suggestion.stop.duration.toLong())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                    modifier = Modifier.testTag("time"))
              }
        }
  }
}
