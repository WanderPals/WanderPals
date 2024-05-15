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
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.StopsListViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.screens.trip.agenda.DisplayDate
import com.github.se.wanderpals.ui.screens.trip.agenda.StopInfoDialog
import java.time.LocalDate

/**
 * Composable function that displays the list of stops for a trip.
 *
 * @param stopsListViewModel The view model that provides the data for the stops list.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "WeekBasedYear")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsList(stopsListViewModel: StopsListViewModel, tripId: String, tripsRepository: TripsRepository) {

  var isStopPressed by remember { mutableStateOf(false) }
  var selectedStopId by remember { mutableStateOf("") }

  val onActivityItemClick = { stopId: String ->
    isStopPressed = true
    selectedStopId = stopId
  }

  // State for managing the loading state
  val isLoading by stopsListViewModel.isLoading.collectAsState()

  val stops by stopsListViewModel.stops.collectAsState()

  val  refreshFunction = { stopsListViewModel.loadStops() }

  LaunchedEffect(Unit) { refreshFunction() }

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
                          color = MaterialTheme.colorScheme.onPrimary))
            },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("BackButton"),
                  colors =
                      IconButtonDefaults.iconButtonColors(
                          contentColor = MaterialTheme.colorScheme.onPrimary)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary),
        )
      }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
          if (!isLoading) {
            if (stops.isNotEmpty()) {
              var dates = stops.map { stop -> stop.date }.distinct()

              // Convert the date strings to LocalDate objects
              val localDates = dates.map { LocalDate.parse(it.toString()) }

              // Sort the LocalDate objects in ascending order
              dates = localDates.sortedBy { it }

              // Display the stops in a LazyColumn
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
                                          .background(MaterialTheme.colorScheme.secondaryContainer),
                                  content = {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                          DisplayDate(
                                              date = date,
                                              color =
                                                  MaterialTheme.colorScheme.onSecondaryContainer)
                                        }
                                  },
                                  contentAlignment = Alignment.CenterStart)
                            }
                            items(
                                stops
                                    .filter { stop -> stop.date == date }
                                    .sortedBy { it.startTime }) { stop ->
                                  StopItem(stop, onActivityItemClick, tripId, tripsRepository, refreshFunction)
                                }
                          }
                        })
                  }
              PullToRefreshLazyColumn(
                  inputLazyColumn = stopsLazyColumn,
                  onRefresh = { stopsListViewModel.loadStops() })
            } else { // Display a message if there are no stops
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
                    onClick = { stopsListViewModel.loadStops() },
                    modifier =
                        Modifier.align(Alignment.Center)
                            .padding(top = 60.dp)
                            .testTag("RefreshButton"),
                    content = { Icon(Icons.Default.Refresh, contentDescription = "Refresh trips") })
              }
            }
          } else { // Display a loading indicator while the data is being fetched
            Box(modifier = Modifier.fillMaxSize()) {
              CircularProgressIndicator(
                  modifier = Modifier.align(Alignment.Center).testTag("Loading").size(50.dp),
                  color = MaterialTheme.colorScheme.primary)
            }
          }
        }
      }
  if (isStopPressed) { // Display the stop information dialog
    val selectedStop = stops.find { stop -> stop.stopId == selectedStopId }!!
    StopInfoDialog(stop = selectedStop, closeDialogueAction = { isStopPressed = false })
  }
}
