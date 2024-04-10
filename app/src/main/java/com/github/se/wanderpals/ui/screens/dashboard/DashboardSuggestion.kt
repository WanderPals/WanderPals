package com.github.se.wanderpals.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DashboardSuggestion(suggestion: Suggestion) {
    Card(modifier = Modifier
        .padding(8.dp)
        .testTag("suggestion${(suggestion.suggestionId)}")) {
        Column(modifier = Modifier.border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp)).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = suggestion.stop.title, modifier = Modifier.testTag("stopTitle"))
                Text(text = "Created at : " + suggestion.createdAt.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)), modifier = Modifier.testTag("createdAt"))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = suggestion.userName, modifier = Modifier.testTag("userName"))
                Text(
                    text = "${suggestion.stop.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))} " +
                            "${suggestion.stop.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} -> \n" +
                            LocalDateTime.of(suggestion.stop.date, suggestion.stop.startTime).plusMinutes(suggestion.stop.duration.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.testTag("time")
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardSuggestionPreview() {
    val stop = Stop(
        stopId = "1",
        title = "Stop Title",
        address = "123 Street",
        date = LocalDate.now(),
        startTime = LocalTime.now(),
        duration = 60,
        budget = 100.0,
        description = "This is a description of the stop. It should be long enough to wrap to the next line. and maybe even more. and even more",
        geoCords = GeoCords(0.0, 0.0),
        website = "https://example.com",
        imageUrl = ""
    )
    val suggestion = Suggestion(
        suggestionId = "1",
        userName = "User",
        createdAt = LocalDate.now(),
        stop = stop,
        text = "This is a suggestion for a stop.",
        userId = "1"
    )
    DashboardSuggestion(suggestion = suggestion)
}