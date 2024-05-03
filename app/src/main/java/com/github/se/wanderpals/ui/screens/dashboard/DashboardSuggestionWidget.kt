package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.theme.backgroundLight
import com.github.se.wanderpals.ui.theme.onPrimaryContainerLight
import com.github.se.wanderpals.ui.theme.primaryContainerLight
import com.github.se.wanderpals.ui.theme.primaryLight
import com.github.se.wanderpals.ui.theme.secondaryLight
import com.github.se.wanderpals.ui.theme.surfaceVariantLight
import com.github.se.wanderpals.ui.theme.tertiaryLight
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DashboardSuggestionWidget(viewModel: DashboardViewModel, onClick: () -> Unit = {}) {
  val suggestionList by viewModel.state.collectAsState()
  val sortedSuggestion = suggestionList.sortedByDescending { it.createdAt }

  Card(
      modifier =
          Modifier.padding(16.dp)
              .fillMaxWidth()
              .clickable(onClick = onClick)
              .testTag("suggestionCard"),
      colors =
          CardDefaults.cardColors(
              containerColor = surfaceVariantLight // This sets the background color of the Card
              ),
      shape = RoundedCornerShape(10.dp)) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                  Modifier.clip(RoundedCornerShape(10.dp))
                      .background(primaryContainerLight)
                      .padding(8.dp)) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "suggestionIcon",
                    tint = onPrimaryContainerLight,
                    modifier = Modifier.testTag("suggestionIcon"))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Suggestions",
                    style =
                        TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            color = onPrimaryContainerLight,
                            letterSpacing = 0.15.sp,
                        ),
                    modifier = Modifier.testTag("suggestionTitle"))
              }

          Spacer(modifier = Modifier.padding(6.dp))

          if (sortedSuggestion.isEmpty()) {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(backgroundLight)
                        .padding(40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                      text = "No suggestions available",
                      style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                      modifier = Modifier.testTag("noSuggestions"))
                }
          } else {
            SuggestionItem(suggestion = sortedSuggestion[0])

            if (sortedSuggestion.size > 1) {
              Spacer(modifier = Modifier.height(8.dp))
              SuggestionItem(suggestion = sortedSuggestion[1])
            }

            if (sortedSuggestion.size > 2) {
              Spacer(modifier = Modifier.height(8.dp))
              SuggestionItem(suggestion = sortedSuggestion[2])
            }
          }
        }
      }
}

@Composable
fun SuggestionItem(suggestion: Suggestion) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier =
          Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(backgroundLight)
              .testTag("suggestionItem" + suggestion.suggestionId)) {
        Column(modifier = Modifier.padding(16.dp, 8.dp)) {
          Text(
              text = suggestion.stop.title,
              style =
                  TextStyle(
                      fontWeight = FontWeight(500),
                      color = primaryLight,
                      fontSize = 15.sp,
                      lineHeight = 20.sp,
                      letterSpacing = 0.15.sp),
              modifier = Modifier.testTag("suggestionTitle" + suggestion.suggestionId))
          Spacer(modifier = Modifier.height(4.dp))
          Text(
              text =
                  "Suggested by ${if(suggestion.userName.length > 10) suggestion.userName.subSequence(0, 10).toString()+"..." else suggestion.userName}",
              style =
                  TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 20.sp,
                      fontWeight = FontWeight(500),
                      color = tertiaryLight,
                      letterSpacing = 0.14.sp,
                  ),
              modifier = Modifier.testTag("suggestionUser" + suggestion.userId))
        }
        // cont
        Column(modifier = Modifier.padding(8.dp)) {
          val startTime = LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
          val endTime = startTime.plusMinutes(suggestion.stop.duration.toLong())
          Text(
              text = startTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
              style =
                  TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 20.sp,
                      fontWeight = FontWeight(500),
                      color = secondaryLight,
                      letterSpacing = 0.14.sp,
                  ),
              modifier = Modifier.testTag("suggestionStart" + suggestion.suggestionId))
          Spacer(modifier = Modifier.height(4.dp))
          Text(
              text = endTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
              style =
                  TextStyle(
                      fontSize = 14.sp,
                      lineHeight = 20.sp,
                      fontWeight = FontWeight(500),
                      color = secondaryLight,
                      letterSpacing = 0.14.sp,
                  ),
              modifier = Modifier.testTag("suggestionEnd" + suggestion.suggestionId))
        }
      }
}
