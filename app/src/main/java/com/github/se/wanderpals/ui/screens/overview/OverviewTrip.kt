package com.github.se.wanderpals.ui.screens.overview

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.theme.onPrimaryContainerLight
import com.github.se.wanderpals.ui.theme.primaryContainerLight
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

fun formatTripTitle(title: String, maxLineLength: Int = 12): String {
  if (title.length <= maxLineLength) return title

  val words = title.split(" ")
  val builder = StringBuilder()
  var currentLineLength = 0

  words.forEach { word ->
    if (currentLineLength + word.length > maxLineLength) {
      if (currentLineLength != 0) {
        builder.append("-\n-")
        currentLineLength = 1 // account for the hyphen at the beginning of the new line
      }
      if (word.length > maxLineLength) {
        // If the word is longer than the max line length, split the word itself
        word.chunked(maxLineLength - 1).forEach { chunk ->
          if (currentLineLength + chunk.length > maxLineLength - 1) {
            builder.append("-\n-")
            currentLineLength = 1
          }
          builder.append(chunk)
          currentLineLength += chunk.length
        }
      } else {
        builder.append(word)
        currentLineLength += word.length
      }
    } else {
      if (currentLineLength > 0) builder.append(" ")
      builder.append(word)
      currentLineLength += word.length + 1 // +1 for the space
    }
  }

  return builder.toString()
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

  // Mutable state to check if the dialog is open
  var dialogIsOpen by remember { mutableStateOf(false) }

  // Use of a launch effect to reset the value of isSelected to false after 100ms
  LaunchedEffect(isSelected.value) {
    if (isSelected.value) {
      delay(100)
      isSelected.value = false
    }
  }

  if (dialogIsOpen) {
    AlertDialog(
        onDismissRequest = { dialogIsOpen = false },
        title = { Text("You are no longer part of this trip.") },
        text = { Text("please refresh the page to see the updated list of trips.") },
        confirmButton = {
          Button(
              onClick = { dialogIsOpen = false },
          ) {
            Text("Close")
          }
        })
  }

  Box(modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)) {
    // Button representing the trip overview
    Button(
        onClick = {
          if (trip.users.find { it == SessionManager.getCurrentUser()!!.userId } != null) {
            dialogIsOpen = false
            SessionManager.setTripName(trip.title)
            navigationActions.setVariablesTrip(trip.tripId)
            navigationActions.navigateTo(Route.TRIP)
          } else {
            dialogIsOpen = true
          }
        },
        modifier =
            Modifier.align(Alignment.TopCenter)
                .width(360.dp)
                .height(130.dp)
                .padding(top = 1.dp)
                .testTag("buttonTrip" + trip.tripId),
        shape = RoundedCornerShape(size = 15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryContainerLight)) {
          // Column containing trip information
          Column(modifier = Modifier.width(320.dp)) {
            Row(
                modifier =
                    Modifier.fillMaxWidth().padding(top = 8.dp) // Ensure padding for visual spacing
                ) {
                  // Trip title
                  Text(
                      text = trip.title,
                      modifier = Modifier.height(24.dp),
                      style =
                          TextStyle(
                              fontSize = 18.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(500),
                              color = onPrimaryContainerLight,
                              letterSpacing = 0.1.sp,
                          ),
                      textAlign = TextAlign.Start)

                  Spacer(Modifier.weight(1f))

                  // Start date
                  Text(
                      text = trip.startDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN)),
                      modifier =
                          Modifier.height(24.dp)
                              .padding(
                                  top = 4.dp), // the padding is for having the text on the same
                      // line and in the same height as the trip title
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(500),
                              color = onPrimaryContainerLight,
                              letterSpacing = 0.1.sp,
                          ),
                      textAlign = TextAlign.End)

                  Spacer(modifier = Modifier.width(11.dp)) // Space between start and end date

                  // End date
                  Text(
                      text = trip.endDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN)),
                      modifier =
                          Modifier.height(24.dp)
                              .padding(
                                  top = 4.dp), // the padding is for having the text on the same
                      // line and in the same height as the trip title
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(500),
                              color = onPrimaryContainerLight,
                              letterSpacing = 0.1.sp,
                          ),
                      textAlign = TextAlign.End)
                }

            Spacer(Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth() // Ensure padding for visual spacing
                ) {
                  Spacer(Modifier.weight(1f)) // Pushes the icon to the end

                  // Share trip code button
                  IconButton(
                      modifier =
                          Modifier.width(24.dp)
                              .height(28.dp)
                              .testTag("shareTripButton" + trip.tripId),
                      onClick = {
                        isSelected.value = true
                        context.shareTripCodeIntent(trip.tripId)
                      }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier =
                                Modifier.background(
                                    if (isSelected.value) Color.LightGray else Color.Transparent))
                      }
                }
          }
        }
  }
}
