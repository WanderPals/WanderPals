package com.github.se.wanderpals.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.google.android.play.core.internal.by
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Overview(overviewViewModel: OverviewViewModel, navigationActions: NavigationActions) {

  val trip1 = Trip(
    tripId = "1",
    title = "Summer Adventure",
    startDate = LocalDate.of(2024, 7, 1),
    endDate = LocalDate.of(2024, 7, 15),
    totalBudget = 2000.0,
    description = "An adventurous trip exploring nature and wildlife.",
    imageUrl = "https://example.com/summer_adventure.jpg",
    stops = listOf("stop1", "stop2", "stop3"),
    users = listOf("user1", "user2", "user3"),
    suggestions = listOf("suggestion1", "suggestion2")
  )

  val trip2 = Trip(
    tripId = "2",
    title = "Winter Ski Trip",
    startDate = LocalDate.of(2024, 12, 20),
    endDate = LocalDate.of(2024, 12, 30),
    totalBudget = 3000.0,
    description = "A ski trip to the snowy mountains.",
    imageUrl = "https://example.com/winter_ski_trip.jpg",
    stops = listOf("ski_resort1", "ski_resort2"),
    users = listOf("user4", "user5"),
    suggestions = listOf("suggestion3", "suggestion4", "suggestion5")
  )

  val trip3 = Trip(
    tripId = "3",
    title = "City Exploration",
    startDate = LocalDate.of(2024, 9, 10),
    endDate = LocalDate.of(2024, 9, 15),
    totalBudget = 1500.0,
    description = "Exploring famous landmarks and enjoying city life.",
    imageUrl = "https://example.com/city_exploration.jpg",
    stops = listOf("city_stop1", "city_stop2"),
    users = listOf("user6", "user7", "user8"),
    suggestions = emptyList()
  )
  var tripList = listOf(trip1, trip2, trip3)
  var searchText by remember { mutableStateOf("") }
  var active by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      Box(modifier = Modifier.padding(start = 13.dp, top = 16.dp)) {
        DockedSearchBar(
          query = searchText,
          onQueryChange = { newTxt -> searchText = newTxt },
          onSearch = {},
          active = false,
          onActiveChange = { active = it },
          placeholder = { Text("Search a trip") },
          trailingIcon = {
            if (searchText.isEmpty()) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = Icons.Default.Search.name,
                modifier = Modifier.size(24.dp)
              )
            } else {
              IconButton(onClick = { searchText = "" }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = Icons.Default.Clear.name,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = Icons.Default.Menu.name,
              modifier = Modifier.size(24.dp)
            )
          }) {

        }
      }
    },
    bottomBar = {
      Box(
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = { navigationActions.navigateTo(Route.CREATE_TRIP) },
          modifier = Modifier
            .width(360.dp)
            .height(80.dp)
            .padding(bottom = 20.dp)
            .align(Alignment.TopCenter),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFDEE1F9)
          )

        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(
              8.dp,
              Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = Icons.Default.Add.name,
              tint = Color(0xFF000000),
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Create a New Trip",
              style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp,
              )
            )
          }
        }
      }
    }
  ) { innerPadding ->

    if (false /*todoList.filterToDosByTitle(searchText).isEmpty()*/) {
      Text(
        text = "Looks like you have no travel plan yet.",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        style = TextStyle(color = Color.Red)
      )
    } else {
      Column(modifier = Modifier.padding(innerPadding)) {
        Text(
          text = "My trip projects",
          modifier = Modifier.padding(horizontal = 27.dp, vertical = 14.dp),
          style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF5A7BF0),
            letterSpacing = 0.5.sp,
          )
        )

        LazyColumn() {
          items(tripList /*.filterToDosByTitle(searchText)*/) { trip ->
            DisplayTrip(trip = trip, navigationActions)


          }
        }
      }
    }
  }
}

@Composable
fun DisplayTrip(trip: Trip, navigationActions: NavigationActions) {
  Box(modifier = Modifier.fillMaxWidth()) {
    Button(
      onClick = { navigationActions.navigateTo(Route.TRIP + "/${trip.tripId}") },
      modifier = Modifier
        .align(Alignment.TopCenter)
        .width(360.dp)
        .height(100.dp)
        .padding(top = 16.dp),
      shape = RoundedCornerShape(size = 15.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFFEAEEFD)
      )
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
      ) {
        Text(
          text = trip.title,
          modifier = Modifier
            .height(24.dp),
          style =
          TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF000000),
            letterSpacing = 0.5.sp,
          )
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "From : %s".format(
            trip.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
          ),
          modifier = Modifier
            .height(24.dp),
          style = TextStyle(
            fontSize = 12.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF000000),
            letterSpacing = 0.5.sp,
          )
        )
        Text(
          text = "To : %s".format(
            trip.endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
          ),
          modifier = Modifier
            .height(24.dp),
          style = TextStyle(
            fontSize = 12.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF000000),
            letterSpacing = 0.5.sp,
          )
        )


      }

    }

  }
}