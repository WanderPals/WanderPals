package com.github.se.wanderpals.ui.screens.suggestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Suggestion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Composable function for displaying the header of a suggestion item.
 *
 * @param suggestion The suggestion object to be displayed.
 */
@Composable
fun SuggestionHeader(suggestion: Suggestion, testTagPrefix: String = "") {
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
                ),
                modifier = Modifier.testTag(testTagPrefix + "Title")
            )
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
                ),
                modifier = Modifier.testTag(testTagPrefix + "CreatedAt")
            )
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            val suggestionStartTime =
                LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime)
            val suggestionEndTime = suggestionStartTime.plusMinutes(suggestion.stop.duration.toLong())
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
                modifier = Modifier.testTag(testTagPrefix + "Start" + suggestion.suggestionId))
//                        modifier = Modifier.testTag("suggestionStart" + suggestion.suggestionId))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = suggestionEndTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                style =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.14.sp,
                ),
                modifier = Modifier.testTag(testTagPrefix + "End" + suggestion.suggestionId)
//                modifier = Modifier.testTag("suggestionEnd" + suggestion.suggestionId)
            )
        }
    }
}


/**
 * Composable function for displaying the description of a suggestion item.
 *
 * @param suggestion The suggestion object to be displayed.
 */
@Composable
fun SuggestionDescription(suggestion: Suggestion, testTag: String = "") {
    Box(
        modifier =
        Modifier.fillMaxWidth()
            .height(55.dp)
            .background(
                MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
        .testTag(testTag)
    ) {
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
}

