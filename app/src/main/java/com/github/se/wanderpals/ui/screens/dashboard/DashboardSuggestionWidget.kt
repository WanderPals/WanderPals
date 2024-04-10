package com.github.se.wanderpals.ui.screens.dashboard

import DashboardTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DashboardSuggestionWidget (viewModel: DashboardViewModel, onClick : () -> Unit = {}) {
    val suggestionList by viewModel.state.collectAsState()
    val sortedSuggestion = suggestionList.sortedByDescending { it.createdAt }

            Card (
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = onClick)
                    .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(16.dp))
                    .testTag("card"),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent // This sets the background color of the Card
                )
            ) {
                Column (
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = "Latest suggestion:",
                        modifier = Modifier
                            .testTag("suggestionTitle")
                            .padding(top = 8.dp, bottom = 8.dp),
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ))

                    if(sortedSuggestion.isEmpty()) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier
                            .padding(top = 16.dp, bottom = 40.dp)
                            .fillMaxWidth()) {
                            Text(
                                text = "No suggestions yet.",
                                modifier = Modifier.testTag("noSuggestions")
                            )
                        }

                    }
                    else {
                        for (i in 0 until minOf(3, sortedSuggestion.size)) {
                            DashboardSuggestion(sortedSuggestion[i])
                            if (i < minOf(3, sortedSuggestion.size) - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 32.dp)
                                        .testTag("divider$i"),
                                    thickness = 1.dp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }



@Preview(showBackground = true)
@Composable
fun DashboardSuggestionWidgetPreview() {
    val stop = Stop(
        stopId = "1",
        title = "Stop Title",
        address = "123 Street",
        date = LocalDate.now(),
        startTime = LocalTime.now(),
        duration = 60,
        budget = 100.0,
        description = "This is a description of the stop. It should be brief and informative.",
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
    val viewModel = DashboardViewModel(tripsRepository = (TripsRepository("a", Dispatchers.IO)), tripId = "a")
    DashboardSuggestionWidget(viewModel = viewModel, onClick = {})
}