package com.github.se.wanderpals.ui.screens.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.format.DateTimeFormatter

/**
 * Composable function that represents an overview of a trip. Displays basic trip information such
 * as title, start date, and end date.
 *
 * @param trip The trip object containing trip details.
 * @param navigationActions The navigation actions used for navigating to detailed trip view.
 */
@Composable
fun OverviewTrip(trip: Trip, navigationActions: NavigationActions) {

  // Date pattern for formatting start and end dates
  val DATE_PATTERN = "dd/MM/yyyy"

  Box(modifier = Modifier.fillMaxWidth()) {
    // Button representing the trip overview
    Button(
        onClick = { navigationActions.navigateTo(Route.TRIP + "/${trip.tripId}") },
        modifier =
            Modifier.align(Alignment.TopCenter)
                .width(360.dp)
                .height(100.dp)
                .padding(top = 16.dp)
                .testTag("buttonTrip" + trip.tripId),
        shape = RoundedCornerShape(size = 15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAEEFD))) {
          // Column containing trip information
          Column(
              modifier = Modifier.fillMaxSize(),
          ) {
            // Trip title
            Text(
                text = trip.title,
                modifier = Modifier.height(24.dp),
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),
                        letterSpacing = 0.5.sp,
                    ))
            Spacer(modifier = Modifier.height(3.dp))
            // Start date
            Text(
                text =
                    "From : %s"
                        .format(trip.startDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN))),
                modifier = Modifier.height(24.dp),
                style =
                    TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),
                        letterSpacing = 0.5.sp,
                    ))
            // End date
            Text(
                text =
                    "To : %s"
                        .format(trip.endDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN))),
                modifier = Modifier.height(24.dp),
                style =
                    TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),
                        letterSpacing = 0.5.sp,
                    ))
          }
        }
  }
}