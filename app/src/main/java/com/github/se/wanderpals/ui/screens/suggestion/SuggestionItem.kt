package com.github.se.wanderpals.ui.screens.suggestion

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.viewmodel.SuggestionsViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.delay

/**
 * Composable function that represents a single suggestion item in the suggestion feed.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param onClick The callback function for handling the click event.
 * @param viewModel The ViewModel for managing suggestions.
 * @param modifier The modifier to be applied to the suggestion item.
 * @param userRole The role of the user.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun SuggestionItem(
    suggestion: Suggestion,
    onClick: () -> Unit,
    viewModel: SuggestionsViewModel,
    modifier: Modifier = Modifier,
    userRole: Role = viewModel.getCurrentUserRole(),
) {
  val isLiked = viewModel.getIsLiked(suggestion.suggestionId)
  val likesCount = viewModel.getNbrLiked(suggestion.suggestionId).toString()

  val isVoteClicked =
      viewModel.getVoteIconClicked(suggestion.suggestionId) // the vote icon is clicked
  val startTime = viewModel.getStartTime(suggestion.suggestionId)

  val initialRemainingTimeFlow = viewModel.getRemainingTimeFlow(suggestion.suggestionId)
  val remainingTime = remember { mutableStateOf(initialRemainingTimeFlow.value) }

  // Set up the "Up" button votable value to true by default
  val isUpVotable = remember { mutableStateOf(true) }

  // set the colors of the card
  val cardColors =
      CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

  LaunchedEffect(key1 = isVoteClicked) { // Start the countdown only if the vote icon is clicked
    if (isVoteClicked &&
        startTime !=
            null) { // Start the countdown only if the vote icon is clicked and the start time is
      // not null
      val endTime = startTime.plusHours(24)
      do {
        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(now, endTime)
        if (duration.isNegative) {
          remainingTime.value = "00:00:00"
          isUpVotable.value = false // Set up votable to false when the countdown reaches zero
          break
        }
        remainingTime.value = duration.toFormattedString()
        delay(1000)
      } while (duration >
          java.time.Duration
              .ZERO) // while the duration is not negative, keep updating the remaining time
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
              .height(166.dp)
              .testTag("suggestion" + suggestion.suggestionId),
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

              // Header
              SuggestionHeader(suggestion)
              Spacer(modifier = Modifier.height(8.dp))

              // Description
              SuggestionDescription(suggestion)

              Spacer(modifier = Modifier.height(12.dp))

              // Footer
              SuggestionFooter(
                  suggestion = suggestion,
                  isLiked = isLiked,
                  likesCount = likesCount,
                  isVoteClicked = isVoteClicked,
                  remainingTime = remainingTime,
                  isUpVotable = isUpVotable,
                  userRole = userRole,
                  viewModel = viewModel)
            }
      }
}

/**
 * Composable function for displaying the footer of a suggestion item.
 *
 * @param suggestion The suggestion object to be displayed.
 * @param isLiked The boolean value indicating whether the suggestion is liked.
 * @param likesCount The number of likes for the suggestion.
 * @param isVoteClicked The boolean value indicating whether the vote icon is clicked.
 * @param remainingTime The remaining time for the countdown.
 * @param isUpVotable The boolean value indicating whether the suggestion is up votable.
 * @param userRole The role of the user.
 * @param viewModel The ViewModel for managing suggestions.
 */
@Composable
fun SuggestionFooter(
    suggestion: Suggestion,
    isLiked: Boolean,
    likesCount: String,
    isVoteClicked: Boolean,
    remainingTime: MutableState<String>,
    isUpVotable: MutableState<Boolean>,
    userRole: Role,
    viewModel: SuggestionsViewModel
) {
  // User and Icons
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    ItemText(text = "Suggested by ${suggestion.userName}")

    Spacer(Modifier.weight(1f)) // push the icons to the right

    Row {
      if (userRole == Role.OWNER || userRole == Role.ADMIN) {
        VoteOrCommentIcon(
            painterResource = R.drawable.vote,
            contentDescription = "Vote",
            tintColor =
                MaterialTheme.colorScheme.tertiary.copy(
                    alpha =
                        if (!isVoteClicked) 1f
                        else 0.5f), // if the icon is not clicked, make it opaque; if
            // the
            // icon is clicked, make it semi-transparent
            isEnabled = !isVoteClicked,
            onClick = { // disable the click if the icon is already clicked
              viewModel.toggleVoteIconClicked(suggestion)
            },
            testTag = "voteIcon")
      }

      // Remaining time (i.e. countdown)
      if (isVoteClicked) { // the remaining time is only displayed if the vote icon
        // is clicked

        ItemText(text = remainingTime.value, "countdownRemainingTime")

        Spacer(Modifier.width(8.dp)) // Space between text and icon
      }
      Icon(
          painter =
              if (isLiked) painterResource(R.drawable.up_filled)
              else painterResource(R.drawable.up_outlined),
          contentDescription = "Up",
          tint =
              (if (isLiked) Color.Red else MaterialTheme.colorScheme.tertiary).copy(
                  alpha =
                      if (isUpVotable.value) 1f else 0.5f), // Make semi-transparent if not votable
          modifier =
              Modifier.size(20.dp)
                  .padding(bottom = 4.dp, end = 4.dp)
                  .clickable(enabled = isUpVotable.value) { // Only clickable if votable
                    viewModel.toggleLikeSuggestion(suggestion)
                  }
                  .testTag("upIcon"))

      ItemText(text = likesCount)

      Spacer(
          modifier = Modifier.width(8.dp)) // 8.dp is the space between the text and the next icon

      VoteOrCommentIcon(
          painterResource = R.drawable.comment,
          contentDescription = "Comment",
          paddingValues = PaddingValues(bottom = 2.dp, end = 4.dp),
      )

      ItemText(text = "${suggestion.comments.size}")

      Spacer(
          modifier = Modifier.width(8.dp)) // 8.dp is the space between the text and the next icon

      Icon(
          imageVector = Icons.Outlined.MoreVert,
          contentDescription = "Options",
          tint = MaterialTheme.colorScheme.tertiary,
          modifier =
              Modifier.size(18.dp) // Make sure to set the size as you did with other
                  // icons
                  .clickable { viewModel.showSuggestionBottomSheet(suggestion) }
                  .testTag("suggestionOptionIcon" + suggestion.suggestionId)
                  .graphicsLayer {
                    rotationZ = 90f // Rotate by 90 degrees
                  })
    }
  }
}

/**
 * Helper function for formatting a duration to a string in the format "HH:mm:ss".
 *
 * @return The formatted string.
 */
fun java.time.Duration.toFormattedString(): String {
  val hours = this.toHours().toString().padStart(2, '0')
  val minutes = (this.toMinutes() % 60).toString().padStart(2, '0')
  val seconds = (this.seconds % 60).toString().padStart(2, '0')
  return "$hours:$minutes:$seconds"
}
