package com.github.se.wanderpals.ui.screens.suggestion

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import com.github.se.wanderpals.ui.theme.backgroundLight
import com.github.se.wanderpals.ui.theme.onBackgroundLight
import com.github.se.wanderpals.ui.theme.primaryLight
import com.github.se.wanderpals.ui.theme.secondaryLight
import com.github.se.wanderpals.ui.theme.surfaceVariantLight
import com.github.se.wanderpals.ui.theme.tertiaryLight
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Composable function that represents a single suggestion item in the suggestion feed.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param modifier The modifier to be applied to the suggestion item.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun SuggestionHistoryItem(
    suggestion: Suggestion,
    tripId: String,
    viewModel: SuggestionsViewModel,
    modifier: Modifier = Modifier
) {
  val likesCount = viewModel.getNbrLiked(suggestion.suggestionId).toString()
  val cardColors = CardDefaults.cardColors(containerColor = surfaceVariantLight)

  Card(
      modifier =
          modifier
              .padding(start = 27.dp, end = 27.dp, top = 16.dp, bottom = 16.dp)
              .fillMaxWidth()
              .height(166.dp)
              .border(width = 1.dp, color = surfaceVariantLight, shape = RoundedCornerShape(10.dp)),
      colors = cardColors) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
          Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.6f).padding(end = 8.dp)) {
              Text(
                  text = suggestion.stop.title,
                  style =
                      TextStyle(
                          fontSize = 15.sp,
                          lineHeight = 20.sp,
                          fontWeight = FontWeight(500),
                          color = primaryLight,
                          letterSpacing = 0.15.sp,
                      ))
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                  text = suggestion.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                  style =
                      TextStyle(
                          fontSize = 14.sp,
                          lineHeight = 20.sp,
                          fontWeight = FontWeight(500),
                          color = secondaryLight,
                          letterSpacing = 0.14.sp,
                      ))
            }
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
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
                  modifier = Modifier.testTag("suggestionHistoryStart" + suggestion.suggestionId))
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
                  modifier = Modifier.testTag("suggestionHistoryEnd" + suggestion.suggestionId))
            }
          }
          Spacer(modifier = Modifier.height(8.dp))

          // Description
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(55.dp)
                      .background(backgroundLight, RoundedCornerShape(10.dp))
                      .padding(8.dp)) {
                Text(
                    text = suggestion.stop.description,
                    style =
                        TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            color = onBackgroundLight,
                            letterSpacing = 0.12.sp,
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
              }

          Spacer(modifier = Modifier.height(12.dp))

          // User and Icons
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Suggested by ${suggestion.userName}",
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            color = tertiaryLight,
                            letterSpacing = 0.14.sp,
                        ))

                Spacer(Modifier.weight(1f)) // push the icons to the right

                Row {
                  Icon(
                      imageVector = Icons.Filled.Favorite,
                      contentDescription = "Like",
                      tint = Color.Red,
                      modifier =
                          Modifier.size(18.dp)
                              .padding(
                                  end = 4.dp) // 4.dp is the space between the icon and the text
                              .testTag(
                                  "staticLikeIconSuggestionHistoryFeedScreen_${suggestion.suggestionId}"))

                  Text(
                      text = likesCount,
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              color = tertiaryLight,
                              letterSpacing = 0.14.sp,
                          ))

                  Spacer(
                      modifier =
                          Modifier.width(
                              8.dp)) // 8.dp is the space between the text and the next icon

                  Icon(
                      imageVector = Icons.Default.MailOutline,
                      contentDescription = null,
                      tint = tertiaryLight,
                      modifier =
                          Modifier.size(18.dp)
                              .padding(
                                  end = 4.dp) // 4.dp is the space between the icon and the text
                      )

                  Text(
                      text = "${suggestion.comments.size}",
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              color = tertiaryLight,
                              letterSpacing = 0.14.sp,
                          ))
                }
              }
        }
      }
}
