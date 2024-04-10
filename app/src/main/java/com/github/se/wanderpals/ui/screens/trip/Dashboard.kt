package com.github.se.wanderpals.ui.screens.trip

import DashboardTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.dashboard.DashboardSuggestionWidget
import java.time.LocalDate
import java.time.LocalTime

val stop =
    Stop(
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
        imageUrl = "")
val suggestion =
    com.github.se.wanderpals.model.data.Suggestion(
        suggestionId = "1",
        userName = "User",
        createdAt = LocalDate.now(),
        stop = stop,
        text = "This is a suggestion for a stop.",
        userId = "1")

/** The Dashboard screen. */
@Composable
fun Dashboard(
    tripId: String,
    dashboardViewModel: DashboardViewModel,
    oldNavAction: NavigationActions
) {
  val isLoading by dashboardViewModel.isLoading.collectAsState()

  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(
          modifier = Modifier.size(50.dp).align(Alignment.Center).testTag("loading"))
    }
  } else {
    Scaffold {
      Surface(
          modifier = Modifier.background(Color.White).padding(it).testTag("dashboardSuggestions")) {
            Column {
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(16.dp)
                          .background(Color.Transparent)
                          .testTag("dashboardTopBar"),
                  contentAlignment = Alignment.Center) {
                    DashboardTopBar()
                  }

              DashboardSuggestionWidget(
                  viewModel = dashboardViewModel,
                  onClick = { oldNavAction.navigateTo(Route.SUGGESTION) })
            }
          }
    }
  }
}
