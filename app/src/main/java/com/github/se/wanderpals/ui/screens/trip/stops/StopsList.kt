package com.github.se.wanderpals.ui.screens.trip.stops

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.StopsListViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.screens.trip.agenda.DisplayDate
import java.time.LocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "WeekBasedYear")
@Composable
fun StopsList(
    stopsListViewModel: StopsListViewModel,
    tripId: String,
    tripsRepository: TripsRepository
) {
  val isLoading by stopsListViewModel.isLoading.collectAsState()
  val stops by stopsListViewModel.stops.collectAsState()
  var isRefreshNeeded by remember { mutableStateOf(false) }

  val refreshFunction = {
    stopsListViewModel.loadStops()
    isRefreshNeeded = false
  }

  LaunchedEffect(isRefreshNeeded) {
    if (isRefreshNeeded) {
      refreshFunction()
    }
  }

  LaunchedEffect(Unit) { refreshFunction() }

  Scaffold(topBar = { StopsListTopAppBar() }) { paddingValues ->
    StopsListContent(
        isLoading = isLoading,
        stops = stops,
        tripId = tripId,
        tripsRepository = tripsRepository,
        onRefreshNeededChange = { isRefreshNeeded = it },
        stopsListViewModel = stopsListViewModel,
        modifier = Modifier.padding(paddingValues))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsListTopAppBar() {
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
}

@Composable
fun StopsListContent(
    isLoading: Boolean,
    stops: List<Stop>,
    tripId: String,
    tripsRepository: TripsRepository,
    onRefreshNeededChange: (Boolean) -> Unit,
    stopsListViewModel: StopsListViewModel,
    modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    if (isLoading) {
      LoadingIndicator()
    } else {
      if (stops.isNotEmpty()) {
        StopsLazyColumn(
            stops = stops,
            tripId = tripId,
            tripsRepository = tripsRepository,
            onRefreshNeededChange = onRefreshNeededChange)
      } else {
        NoStopsMessage(
            onRefresh = { stopsListViewModel.loadStops() },
            isNetworkAvailable = SessionManager.getIsNetworkAvailable())
      }
    }
  }
}

@Composable
fun LoadingIndicator() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
  }
}

@Composable
fun StopsLazyColumn(
    stops: List<Stop>,
    tripId: String,
    tripsRepository: TripsRepository,
    onRefreshNeededChange: (Boolean) -> Unit
) {
  var dates = stops.map { stop -> stop.date }.distinct()

  val localDates = dates.map { LocalDate.parse(it.toString()) }
  dates = localDates.sortedBy { it }

  val stopsLazyColumn =
      @Composable {
        LazyColumn(
            content = {
              dates.forEach { date ->
                item { DateBox(date = date) }
                items(stops.filter { stop -> stop.date == date }.sortedBy { it.startTime }) { stop
                  ->
                  StopItem(
                      stop = stop,
                      tripId = tripId,
                      tripsRepository = tripsRepository,
                      onDelete = { onRefreshNeededChange(true) })
                }
              }
            })
      }

  PullToRefreshLazyColumn(
      inputLazyColumn = stopsLazyColumn, onRefresh = { onRefreshNeededChange(true) })
}

@Composable
fun DateBox(date: LocalDate) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("DateBox")
              .background(MaterialTheme.colorScheme.secondaryContainer),
      contentAlignment = Alignment.CenterStart) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
          DisplayDate(date = date, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
      }
}

@Composable
fun NoStopsMessage(onRefresh: () -> Unit, isNetworkAvailable: Boolean) {
  Box(modifier = Modifier.fillMaxSize()) {
    Text(
        text = if (isNetworkAvailable) "No stops for this trip" else "No internet connection",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(16.dp).testTag("NoActivitiesMessage").align(Alignment.Center))
    IconButton(
        enabled = isNetworkAvailable,
        onClick = onRefresh,
        modifier = Modifier.align(Alignment.Center).padding(top = 60.dp).testTag("RefreshButton")) {
          Icon(Icons.Default.Refresh, contentDescription = "Refresh trips")
        }
  }
}
