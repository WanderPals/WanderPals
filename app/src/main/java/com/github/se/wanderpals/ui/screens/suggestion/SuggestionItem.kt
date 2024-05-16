package com.github.se.wanderpals.ui.screens.suggestion

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.github.se.wanderpals.model.data.User
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

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
    modifier: Modifier = Modifier
) {
  val isLiked = viewModel.getIsLiked(suggestion.suggestionId)
  val likesCount = viewModel.getNbrLiked(suggestion.suggestionId).toString()
  val cardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

    val remainingTime: MutableState<Duration?> = remember { mutableStateOf(viewModel.getRemainingVoteTime(suggestion.suggestionId)) }


    // Get the user from the view model of the suggestion to display the user's role
    val userState: MutableState<User?> = remember { mutableStateOf(null) }

    // Calculate the remaining time
    LaunchedEffect(suggestion) {
        // Fetch user details
        val user = viewModel.getUser(suggestion.userId)
        userState.value = user

        viewModel.startVote(suggestion.suggestionId)
//            val now = LocalDateTime.now()
//            val endTime = now.plusHours(24)
//            if (endTime.isAfter(now)) {
//                remainingTime.value = Duration.between(now, endTime)
//            }
        }

//    // Update the remaining time every second
//    LaunchedEffect(remainingTime.value) {
//        remainingTime.value?.let {
//            delay(1000)
//            if (it.minusSeconds(1).isZero || it.minusSeconds(1).isNegative) {
//                remainingTime.value = null
//            } else {
//                remainingTime.value = it.minusSeconds(1)
//            }
//        }
//    }

//    LaunchedEffect(remainingTime.value) {
//        remainingTime.value?.let {
//            if (it.isZero || it.isNegative) {
//                remainingTime.value = null
//            } else {
//                delay(1000)
//                remainingTime.value = it.minusSeconds(1)
//            }
//        }
//    }

    Card(
      modifier =
          modifier
              .padding(start = 27.dp, end = 27.dp, top = 16.dp, bottom = 16.dp)
              .fillMaxWidth()
              .height(166.dp)
              .border(width = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
              .clickable(onClick = onClick),
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
              val startTime = LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
              val endTime = startTime.plusMinutes(suggestion.stop.duration.toLong())
              Text(
                  text = startTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
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
                      .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
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
//                    if (userState.value?.role == Role.ADMIN || userState.value?.role == Role.OWNER) {
//
//                    }

                    if (remainingTime.value != null) {
                        val hours = remainingTime.value!!.toHours()
                        val minutes = remainingTime.value!!.toMinutes() % 60
                        val seconds = remainingTime.value!!.seconds % 60
                        Text(
                            text =
                            String.format(
                                "%02d:%02d:%02d",
                                hours,
                                minutes,
                                seconds
                            ),
                            style =
                            TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.tertiary,
                                letterSpacing = 0.14.sp,
                            ))
                    }

                    Spacer(
                        modifier =
                        Modifier.width(
                            8.dp)) // 8.dp is the space between

                  Icon(
                      painter =
                          if (isLiked) painterResource(R.drawable.up_filled) else painterResource(R.drawable.up_outlined),
                      contentDescription = "Like",
                      tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.tertiary,
                      modifier =
                          Modifier.size(18.dp)
                              .padding(
                                  end = 4.dp) // 4.dp is the space between the icon and the text
                              .clickable { viewModel.toggleLikeSuggestion(suggestion) }
                              .testTag("likeIconSuggestionFeedScreen_${suggestion.suggestionId}")
                  )

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
                      imageVector = Icons.Default.MailOutline,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.tertiary,
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
                                  18.dp) // Make sure to set the size as you did with other icons
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
