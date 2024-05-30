package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R

/**
 * Composable function for displaying the top bar in the Suggestion screen. Provides navigation
 * options and a back button.
 *
 * @param onHistoryClick Callback function for handling history button click.
 */
@Composable
fun SuggestionTopBar(
    onHistoryClick: () -> Unit // Callback function for handling history button click
) {
  Column(
      modifier = Modifier.testTag("suggestionTopBar"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.surfaceTint)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center) {
                    // Title for the list of suggestions in the top bar
                    Text(
                        text = "Suggestions",
                        modifier =
                            Modifier.padding(start = 20.dp, top = 12.dp)
                                .height(35.dp)
                                .testTag("suggestionTitle"),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                  }

              Spacer(
                  modifier =
                      Modifier.width(160.dp)) // Add space between the title and the history button

              IconButton(
                  onClick = { onHistoryClick() },
                  modifier =
                      Modifier.testTag("suggestionHistoryButtonExists").padding(horizontal = 16.dp),
              ) {
                Icon(
                    painter = painterResource(R.drawable.history),
                    contentDescription = "History",
                    modifier = Modifier.size(32.dp).testTag("suggestionHistoryIconExists"),
                    tint = MaterialTheme.colorScheme.onPrimary)
              }
            }
      }

  Spacer(modifier = Modifier.height(8.dp))
}
