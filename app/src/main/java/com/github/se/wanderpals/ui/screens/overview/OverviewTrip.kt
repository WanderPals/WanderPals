package com.github.se.wanderpals.ui.screens.overview

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

/**
 * Share the trip code using an intent.
 *
 * Creates an intent to share the trip code with other apps and displays a chooser dialog for the
 * user to select an app
 *
 * @param tripId The trip code to be shared.
 */
fun Context.shareTripCodeIntent(tripId: String) {

  val sendIntent =
      Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, tripId)
        type = "text/plain"
      }
  val shareIntent = Intent.createChooser(sendIntent, null)

  startActivity(shareIntent)
}

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

  // Local context
  val context = LocalContext.current

  // Mutable state to check if the icon button for sharing the trip is selected
  val isSelected = remember { mutableStateOf(false) }

  // Use of a launch effect to reset the value of isSelected to false after 100ms
  LaunchedEffect(isSelected.value) {
    if (isSelected.value) {
      delay(100)
      isSelected.value = false
    }
  }

  Box(modifier = Modifier.fillMaxWidth()) {
    // Button representing the trip overview
    Button(
        onClick = {
          SessionManager.setTripName(trip.title)
          navigationActions.setVariablesTrip(trip.tripId)
          navigationActions.navigateTo(Route.TRIP)
        },
        modifier =
            Modifier.align(Alignment.TopCenter)
                .width(360.dp)
                .height(100.dp)
                .padding(top = 16.dp)
                .testTag("buttonTrip" + trip.tripId),
        shape = RoundedCornerShape(size = 15.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer)) {
          // Column containing trip information
          Column(modifier = Modifier.width(320.dp)) {
            // Trip title
            Text(
                text = trip.title,
                modifier = Modifier.height(24.dp),
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 0.5.sp,
                    ))
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  // Start date
                  Text(
                      text =
                          "From : %s"
                              .format(
                                  trip.startDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN))),
                      modifier = Modifier.height(24.dp),
                      style =
                          TextStyle(
                              fontSize = 12.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                              letterSpacing = 0.5.sp,
                          ))
                  // Share trip code button
                  IconButton(
                      modifier = Modifier.size(20.dp).testTag("shareTripButton" + trip.tripId),
                      onClick = {
                        isSelected.value = true
                        context.shareTripCodeIntent(trip.tripId)
                      }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier =
                                Modifier.background(
                                    if (isSelected.value) Color.LightGray else Color.Transparent))
                      }
                }
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 0.5.sp,
                    ))
          }
        }
  }
}
