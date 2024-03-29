package com.github.se.wanderpals.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

    val tripsList by overviewViewModel.state.collectAsState()
    val isLoading by overviewViewModel.isLoading.collectAsState()

    val EMPTY_SEARCH = ""
    var searchText by remember { mutableStateOf(EMPTY_SEARCH) }
    var active by remember { mutableStateOf(false) }


    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    } else {
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
                                IconButton(onClick = { searchText = EMPTY_SEARCH }) {
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
                Column(
                    modifier = Modifier.padding(bottom = 30.dp)
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { /* Actions à exécuter lors du clic */ },
                            modifier = Modifier
                                .width(300.dp)
                                .height(60.dp)
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
                                    imageVector = Icons.Default.Share,
                                    contentDescription = Icons.Default.Share.name,
                                    tint = Color(0xFF000000),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Join a trip",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight(500),
                                        color = Color(0xFF000000),
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    )
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { /* Actions à exécuter lors du clic */ },
                            modifier = Modifier
                                .width(300.dp)
                                .height(60.dp)
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
                                    text = "Create a new trip",
                                    style = TextStyle(
                                        fontSize = 18.sp,
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
            }
        ) { innerPadding ->
            if (tripsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()){

                    Text(
                        modifier = Modifier.align(Alignment.Center).width(260.dp).height(55.dp),
                        text = "Looks like you have no travel plan yet. ",
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                }
            } else {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)) {
                    val filteredTripsByTitle =
                        if (searchText.isEmpty()) {
                            tripsList
                        } else {
                            tripsList.filter { trip ->
                                trip.title.lowercase().contains(searchText.lowercase())
                            }
                        }

                    if (filteredTripsByTitle.isEmpty()) {
                        Text(
                            text = "No trip found.",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(500),
                                color = Color.Gray,
                                letterSpacing = 0.5.sp,
                            )
                        )
                    } else {
                        Text(
                            text = "My trip projects",
                            modifier = Modifier.padding(horizontal = 27.dp, vertical = 14.dp),
                            style = TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF5A7BF0),
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                            ),
                            textAlign = TextAlign.Center
                        )
                        LazyColumn() {
                            items(
                                filteredTripsByTitle
                            ) { trip ->
                                DisplayTrip(trip = trip, navigationActions)
                            }
                        }
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