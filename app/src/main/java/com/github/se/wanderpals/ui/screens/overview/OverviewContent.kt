package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.ui.navigation.NavigationActions

@Composable
fun OverviewContent(
    innerPadding: PaddingValues,
    navigationActions: NavigationActions,
    tripsList: List<Trip>,
    searchText: String
) {

    val filteredTripsByTitle =
        if (searchText.isEmpty()) {
            tripsList
        } else {
            tripsList.filter { trip -> trip.title.lowercase().contains(searchText.lowercase()) }
        }

    if (tripsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(260.dp)
                    .height(55.dp),
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {


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
                    modifier = Modifier.padding(start = 27.dp, top = 15.dp),
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
                        OverviewTrip(trip = trip, navigationActions = navigationActions)
                    }
                }
            }

        }
    }
}