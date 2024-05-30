package com.github.se.wanderpals.ui.screens.suggestion

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

/**
 * Composable function that represents a single suggestion item in the suggestion feed.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param modifier The modifier to be applied to the suggestion item.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun SuggestionItem(
    suggestion: Suggestion,
    onClick: () -> Unit,
    tripId: String,
    viewModel: SuggestionsViewModel,
    modifier: Modifier = Modifier,
    userRole: Role = viewModel.getCurrentUserRole(),
) {
  val isLiked = viewModel.getIsLiked(suggestion.suggestionId)
  val likesCount = viewModel.getNbrLiked(suggestion.suggestionId).toString()

  val isVoteClicked =
      viewModel.getVoteIconClicked(suggestion.suggestionId) // the vote icon is clicked
  val startTime = viewModel.getStartTime(suggestion.suggestionId)
  Log.d("SuggestionItem", "isVoteClicked for suggestion ${suggestion.suggestionId}: $isVoteClicked")

  // set the colors of the card
  val cardColors =
      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

  val initialRemainingTimeFlow = viewModel.getRemainingTimeFlow(suggestion.suggestionId)
  //    val remainingTime = remember { mutableStateOf("23:59:59") }
  val remainingTime = remember { mutableStateOf(initialRemainingTimeFlow.value) }

  LaunchedEffect(key1 = isVoteClicked) { // Start the countdown only if the vote icon is clicked
    if (isVoteClicked &&
        startTime != null) { // Start the countdown only if the vote icon is clicked
      val endTime = startTime.plusHours(24)
      var now: LocalDateTime
      var duration: java.time.Duration

      do {
        now = LocalDateTime.now()
        duration = java.time.Duration.between(now, endTime)
        if (duration.isNegative) {
          remainingTime.value = "00:00:00"
          break
        }
        val hours = duration.toHours().toString().padStart(2, '0')
        val minutes = (duration.toMinutes() % 60).toString().padStart(2, '0')
        val seconds = (duration.seconds % 60).toString().padStart(2, '0')
        remainingTime.value = "$hours:$minutes:$seconds"
        delay(1000)
      } while (duration >
          java.time.Duration
              .ZERO) // while the duration is not negative and the vote icon is clicked (because
      // inside the if(isVoteClicked) block)
    }
  }

  ElevatedCard(
      modifier =
          modifier
              .padding(
                  start = 27.dp,
                  end = 27.dp,
                  top = 12.dp,
                  bottom = 12.dp) // the padding between the screen and the suggestionItem
              .fillMaxWidth()
              .height(166.dp),
      colors = cardColors,
      shape = RoundedCornerShape(6.dp),
      elevation = CardDefaults.cardElevation(10.dp),
      onClick = onClick) {
        Column(
            modifier =
                Modifier.padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState())) { // Add vertical scrolling for testing purposes on
              // small phones
              Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth(0.6f).padding(end = 8.dp)) {
                  Text(
                      text = suggestion.stop.title,
                      style =
                          TextStyle(
                              fontSize = 15.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.primary,
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
                              color = MaterialTheme.colorScheme.secondary,
                              letterSpacing = 0.14.sp,
                          ))
                }
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                  val suggestionStartTime =
                      LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
                  val endTime = suggestionStartTime.plusMinutes(suggestion.stop.duration.toLong())
                  Text(
                      text =
                          suggestionStartTime.format(
                              DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.secondary,
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
                              color = MaterialTheme.colorScheme.secondary,
                              letterSpacing = 0.14.sp,
                          ),
                      modifier = Modifier.testTag("suggestionEnd" + suggestion.suggestionId))
                }
              }
              Spacer(modifier = Modifier.height(8.dp))

              // Description
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(55.dp)
                          .background(
                              MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                          .padding(8.dp)) {
                    Text(
                        text = suggestion.stop.description,
                        style =
                            TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.onBackground,
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
                                color = MaterialTheme.colorScheme.tertiary,
                                letterSpacing = 0.14.sp,
                            ))

                    Spacer(Modifier.weight(1f)) // push the icons to the right

                    Row {
                      if (userRole == Role.OWNER || userRole == Role.ADMIN) {
                        Icon(
                            painter = painterResource(R.drawable.vote),
                            contentDescription = "Vote",
                            tint =
                                MaterialTheme.colorScheme.tertiary.copy(
                                    alpha =
                                        if (!isVoteClicked) 1f
                                        else
                                            0.5f), // if the icon is not clicked, make it opaque; if
                            // the
                            // icon is clicked, make it semi-transparent
                            modifier =
                                Modifier.size(20.dp)
                                    .padding(
                                        bottom = 4.dp,
                                        end = 4.dp // end=4.dp is the space between the icon and
                                        // the text
                                        )
                                    .clickable(
                                        enabled =
                                            !isVoteClicked) { // disable the click if the icon is
                                          // already clicked
                                          Log.d(
                                              "SuggestionItem",
                                              "Vote icon clicked for suggestion ${suggestion.suggestionId}")
                                          viewModel.toggleVoteIconClicked(suggestion)
                                        }
                                    .testTag("voteIcon"))
                      }

                      // Remaining time (i.e. countdown)
                      if (isVoteClicked) { // the remaining time is only displayed if the vote icon
                        // is clicked //todo: create test for this
                        Text(
                            text = remainingTime.value,
                            style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight(500),
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.14.sp,
                                ),
                            modifier = Modifier.testTag("countdownRemainingTime"))

                        Spacer(Modifier.width(8.dp)) // Space between text and icon
                      }
                      Icon(
                          painter =
                              if (isLiked) painterResource(R.drawable.up_filled)
                              else painterResource(R.drawable.up_outlined),
                          contentDescription = "Up",
                          tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.tertiary,
                          modifier =
                              Modifier.size(20.dp)
                                  .padding(bottom = 4.dp, end = 4.dp)
                                  .clickable { viewModel.toggleLikeSuggestion(suggestion) }
                                  .testTag("upIcon"))

                      Text(
                          text = likesCount,
                          style =
                              TextStyle(
                                  fontSize = 14.sp,
                                  lineHeight = 20.sp,
                                  fontWeight = FontWeight(500),
                                  color = MaterialTheme.colorScheme.tertiary,
                                  letterSpacing = 0.14.sp,
                              ))

                      Spacer(
                          modifier =
                              Modifier.width(
                                  8.dp)) // 8.dp is the space between the text and the next icon

                      Icon(
                          painter = painterResource(R.drawable.comment),
                          contentDescription = "Comment",
                          tint = MaterialTheme.colorScheme.tertiary,
                          modifier =
                              Modifier.size(20.dp)
                                  .padding(
                                      bottom = 2.dp,
                                      end =
                                          4.dp) // 4.dp is the space between the texts and the icon
                          )

                      Text(
                          text = "${suggestion.comments.size}",
                          style =
                              TextStyle(
                                  fontSize = 14.sp,
                                  lineHeight = 20.sp,
                                  fontWeight = FontWeight(500),
                                  color = MaterialTheme.colorScheme.tertiary,
                                  letterSpacing = 0.14.sp,
                              ))

                      Spacer(
                          modifier =
                              Modifier.width(
                                  8.dp)) // 8.dp is the space between the text and the next icon

                      Icon(
                          imageVector = Icons.Outlined.MoreVert,
                          contentDescription = "Options",
                          tint = MaterialTheme.colorScheme.tertiary,
                          modifier =
                              Modifier.size(
                                      18.dp) // Make sure to set the size as you did with other
                                  // icons
                                  .clickable { viewModel.showSuggestionBottomSheet(suggestion) }
                                  .testTag("suggestionOptionIcon" + suggestion.suggestionId)
                                  .graphicsLayer {
                                    rotationZ = 90f // Rotate by 90 degrees
                                  })
                    }
                  }
            }
      }
}
