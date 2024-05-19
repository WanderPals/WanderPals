package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DISPLAY_COUNT = 3

/**
 * The Suggestion widget for the dashboard screen.
 *
 * @param viewModel The ViewModel for managing the dashboard screen.
 * @param onClick The action to perform when the widget is clicked.
 */
@Composable
fun DashboardSuggestionWidget(viewModel: DashboardViewModel, onClick: () -> Unit = {}) {
  val suggestionList by viewModel.state.collectAsState()
  val sortedSuggestion = suggestionList.sortedByDescending { it.createdAt }

  ElevatedCard(
      modifier =
          Modifier.padding(horizontal = 16.dp)
              .fillMaxWidth()
              .clickable(onClick = onClick)
              .testTag("suggestionCard"),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  MaterialTheme.colorScheme
                      .surfaceVariant // This sets the background color of the Card
              ),
      shape = RoundedCornerShape(6.dp),
      elevation = CardDefaults.cardElevation(10.dp)) {
        // Suggestion Widget
        Row(
            modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
              // List of Suggestio
              Column(
                  modifier =
                      Modifier.padding(start = 8.dp, top = 8.dp, end = 4.dp, bottom = 8.dp)
                          .fillMaxWidth()) {
                    // Top part of the texts
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.Start,
                              modifier =
                                  Modifier.clip(RoundedCornerShape(4.dp))
                                      .background(MaterialTheme.colorScheme.primaryContainer)
                                      .padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Suggestion Icon",
                                    modifier = Modifier.size(16.dp).testTag("suggestionIcon"),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    text = "Suggestions",
                                    modifier = Modifier.testTag("suggestionTitle"),
                                    style =
                                        TextStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold))
                              }

                          Spacer(modifier = Modifier.padding(4.dp))

                          Text(
                              text =
                                  "Total: ${sortedSuggestion.size} suggestion" +
                                      if (sortedSuggestion.size > 1) "s" else "",
                              modifier =
                                  Modifier.testTag("totalSuggestions")
                                      .clip(RoundedCornerShape(4.dp))
                                      .background(MaterialTheme.colorScheme.surface)
                                      .padding(horizontal = 8.dp, vertical = 4.dp),
                              style =
                                  TextStyle(
                                      color = MaterialTheme.colorScheme.primary,
                                      fontWeight = FontWeight.Bold))
                        }

                    Spacer(modifier = Modifier.padding(4.dp))

                    // List of suggestions
                    Box(
                        modifier =
                            Modifier.clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .fillMaxWidth()) {
                          if (sortedSuggestion.isEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                    Modifier.padding(top = 16.dp, bottom = 40.dp).fillMaxSize()) {
                                  Text(
                                      text = "No suggestions yet.",
                                      modifier = Modifier.testTag("noSuggestions"),
                                      style = TextStyle(color = MaterialTheme.colorScheme.primary),
                                  )
                                }
                          } else {
                            Column {
                              SuggestionWidgetItem(suggestion = sortedSuggestion[0])
                              HorizontalDivider(
                                  color = MaterialTheme.colorScheme.surfaceVariant,
                                  thickness = 1.dp,
                                  modifier = Modifier.padding(horizontal = 8.dp))
                              if (sortedSuggestion.size > 1) {
                                SuggestionWidgetItem(suggestion = sortedSuggestion[1])
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 8.dp))
                              }
                              if (sortedSuggestion.size > 2) {
                                SuggestionWidgetItem(suggestion = sortedSuggestion[2])
                              } else {
                                Box(modifier = Modifier.fillMaxSize())
                              }
                            }
                          }
                        }
                  }
            }
      }
}

/**
 * The Suggestion item for the dashboard screen.
 *
 * @param suggestion The suggestion to display.
 */
@Composable
fun SuggestionWidgetItem(suggestion: Suggestion) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth().testTag("suggestionItem" + suggestion.suggestionId)) {
        Column(modifier = Modifier.padding(8.dp).weight(1f).fillMaxWidth()) {
          Text(
              text =
                  if (suggestion.stop.title.length > 20)
                      suggestion.stop.title.subSequence(0, 18).toString() + "..."
                  else suggestion.stop.title,
              style =
                  TextStyle(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 15.sp),
              modifier = Modifier.testTag("suggestionTitle" + suggestion.suggestionId))
          Spacer(modifier = Modifier.height(4.dp))
          Text(
              text =
                  LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
                      .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
              style = TextStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp),
              modifier = Modifier.testTag("suggestionStart" + suggestion.suggestionId))
        }
        Column(modifier = Modifier.padding(8.dp).weight(1f).fillMaxWidth()) {
          val text = suggestion.userName
          Text(
              text = if (text.length > 40) text.subSequence(0, 38).toString() + "..." else text,
              style =
                  TextStyle(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 15.sp,
                      textAlign = TextAlign.End),
              modifier =
                  Modifier.testTag("suggestionUser" + suggestion.suggestionId).fillMaxWidth())

          Spacer(modifier = Modifier.height(4.dp))

          Text(
              text =
                  LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
                      .plusMinutes(suggestion.stop.duration.toLong())
                      .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
              style =
                  TextStyle(
                      color = MaterialTheme.colorScheme.tertiary,
                      fontSize = 10.sp,
                      textAlign = TextAlign.End),
              modifier = Modifier.testTag("suggestionEnd" + suggestion.suggestionId).fillMaxWidth())
        }
      }
}
