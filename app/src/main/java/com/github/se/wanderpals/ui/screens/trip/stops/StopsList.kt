package com.github.se.wanderpals.ui.screens.trip.stops

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.viewmodel.StopsListViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.screens.trip.agenda.ActivityItem
import com.github.se.wanderpals.ui.screens.trip.agenda.DisplayDate
import com.github.se.wanderpals.ui.screens.trip.agenda.StopInfoDialog
import java.time.LocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "WeekBasedYear")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsList(stopsListviewModel: StopsListViewModel) {

  var isStopPressed by remember { mutableStateOf(false) }
  var selectedStopId by remember { mutableStateOf("") }

  val onActivityItemClick = { stopId: String ->
    isStopPressed = true
    selectedStopId = stopId
  }

  // State for managing the loading state
  val isLoading by stopsListviewModel.isLoading.collectAsState()

  val stops by stopsListviewModel.stops.collectAsState()

  LaunchedEffect(Unit) { stopsListviewModel.loadStops() }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = "Stops",
                  modifier = Modifier.testTag("StopsListTitle"),
                  style =
                      TextStyle(
                          fontWeight = FontWeight.Bold,
                          fontSize = 20.sp,
                          color = MaterialTheme.colorScheme.onPrimaryContainer))
            },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("BackButton"),
                  colors =
                      IconButtonDefaults.iconButtonColors(
                          contentColor = MaterialTheme.colorScheme.onPrimaryContainer)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        )
      }) { it ->
        Column(modifier = Modifier.padding(it)) {
          if (!isLoading) {
            if (stops.isNotEmpty()) {
              var dates = stops.map { stop -> stop.date }.distinct()

              // Convert the date strings to LocalDate objects
              val localDates = dates.map { LocalDate.parse(it.toString()) }

              // Sort the LocalDate objects in ascending order
              dates = localDates.sortedBy { it }

              val stopsLazyColumn =
                  @Composable {
                    LazyColumn(
                        content = {
                          dates.forEach { date ->
                            item {
                              Box(
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .weight(1f)
                                          .testTag("DateBox")
                                          .background(MaterialTheme.colorScheme.tertiaryContainer),
                                  content = {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                          DisplayDate(
                                              date = date,
                                              color = MaterialTheme.colorScheme.onTertiaryContainer)
                                        }
                                  },
                                  contentAlignment = Alignment.CenterStart)
                            }
                            items(
                                stops
                                    .filter { stop -> stop.date == date }
                                    .sortedBy { it.startTime }) { stop ->
                                  ActivityItem(stop, onActivityItemClick)
                                }
                          }
                        })
                  }
              PullToRefreshLazyColumn(
                  inputLazyColumn = stopsLazyColumn, onRefresh = { stopsListviewModel.loadStops() })
            } else {
              Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No stops for this trip",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier =
                        Modifier.padding(16.dp)
                            .testTag("NoActivitiesMessage")
                            .align(Alignment.Center))
                IconButton(
                    onClick = { stopsListviewModel.loadStops() },
                    modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
                    content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh trips") })
              }
            }
          } else {
            Box(modifier = Modifier.fillMaxSize()) {
              CircularProgressIndicator(
                  modifier = Modifier.align(Alignment.Center).testTag("Loading").size(50.dp),
                  color = MaterialTheme.colorScheme.primary)
            }
          }
        }
      }
  if (isStopPressed) {
    val selectedStop = stops.find { stop -> stop.stopId == selectedStopId }!!
    StopInfoDialog(stop = selectedStop, closeDialogueAction = { isStopPressed = false })
  }
}
