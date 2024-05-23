package com.github.se.wanderpals.ui.screens.trip.stops

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.StopItemViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.theme.outlineVariantLight

/**
 * Composable function that displays a stop item in the list of stops for a trip.
 *
 * @param stop The stop to display.
 * @param tripId The identifier of the trip to which the stop belongs.
 * @param tripsRepository The repository for trips.
 * @param onDelete Callback function triggered when the stop is deleted.
 */
@Composable
fun StopItem(stop: Stop, tripId: String, tripsRepository: TripsRepository, onDelete: () -> Unit) {

  val stopItemViewModel: StopItemViewModel =
      viewModel(
          factory =
              StopItemViewModel.StopItemViewModelFactory(
                  tripsRepository = tripsRepository, tripId = tripId))

  // State to handle the visibility of the confirmation dialog
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }
  var showNoRightsToast by remember { mutableStateOf(false) }

  if (showNoRightsToast) {
    ShowToast("You do not have the rights to delete this stop")
    showNoRightsToast = false
  }

  val isDeleted by stopItemViewModel.isDeleting.collectAsState()
  if (isDeleted) {
    onDelete()
    stopItemViewModel.resetDeleteState()
  }

  var isStopPressed by remember { mutableStateOf(false) }

  val stopHasLocation = stop.geoCords.latitude != 0.0 || stop.geoCords.longitude != 0.0
  Box(modifier = Modifier.testTag(stop.stopId).fillMaxWidth()) {
    Button(
        onClick = { isStopPressed = true },
        shape = RectangleShape,
        modifier =
            Modifier.height(100.dp).fillMaxWidth().testTag("activityItemButton" + stop.stopId),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
          Row(modifier = Modifier.fillMaxSize()) {
            // Texts Column
            Column(
                modifier =
                    Modifier.weight(
                            1f) // Takes up all available space, pushing the IconButton to the right
                        .align(Alignment.CenterVertically) // Vertically center the column content
                        .fillMaxSize(), // Fill the available space
                verticalArrangement = Arrangement.SpaceEvenly) {
                  Text(
                      text = stop.title,
                      style =
                          TextStyle(
                              fontSize = 16.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              letterSpacing = 0.16.sp),
                      color = MaterialTheme.colorScheme.primary,
                      modifier =
                          Modifier.wrapContentWidth(Alignment.Start)
                              .testTag("ActivityTitle" + stop.stopId))
                  Text(
                      text =
                          "${stop.startTime} - ${stop.startTime.plusMinutes(stop.duration.toLong())}",
                      style =
                          TextStyle(
                              fontSize = 16.sp,
                              lineHeight = 20.sp,
                              fontWeight = FontWeight(500),
                              letterSpacing = 0.16.sp,
                          ),
                      color = MaterialTheme.colorScheme.secondary,
                      modifier =
                          Modifier.wrapContentWidth(Alignment.Start)
                              .testTag("ActivityTime" + stop.stopId))
                  if (stopHasLocation) {
                    Text(
                        text = stop.address,
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(500),
                                letterSpacing = 0.16.sp),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier =
                            Modifier.wrapContentWidth(Alignment.Start)
                                .testTag("ActivityAddress" + stop.stopId))
                  }
                }

            // Map Navigation Button
            // Icon Button at the far right, centered vertically
            IconButton(
                onClick = {
                  if (stopHasLocation) {
                    navigationActions.setVariablesLocation(stop.geoCords, stop.address)
                    navigationActions.navigateTo(Route.MAP)
                  }
                },
                modifier =
                    Modifier.size(24.dp) // Adjust the size of the IconButton as needed
                        .align(Alignment.CenterVertically)
                        .testTag(
                            "navigationToMapButton" +
                                stop.stopId), // Center the IconButton vertically within the
                enabled = stopHasLocation
                // Row
                ) {
                  Icon(
                      imageVector = Icons.Default.LocationOn,
                      tint =
                          if (stopHasLocation) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                      contentDescription = null // Provide an appropriate content description
                      )
                }

            // Add spacing between the map icon and delete icon
            Spacer(modifier = Modifier.width(16.dp))

            // Delete Icon
            IconButton(
                onClick = {
                  if (SessionManager.isAdmin()) {
                    showDeleteConfirmDialog = true
                  } else {
                    showNoRightsToast = true
                  }
                },
                modifier =
                    Modifier.size(24.dp)
                        .align(Alignment.CenterVertically)
                        .testTag("deleteButton" + stop.stopId),
                content = {
                  Icon(
                      imageVector = Icons.Default.Delete,
                      tint =
                          if (SessionManager.isAdmin()) MaterialTheme.colorScheme.error
                          else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                      contentDescription = "Delete Stop")
                })
          }
        }
  }

  // Confirmation Dialog for Deletion
  if (showDeleteConfirmDialog) {
    AlertDialog(
        onDismissRequest = { showDeleteConfirmDialog = false },
        title = { Text("Confirm Deletion") },
        text = {
          Text(
              when (SessionManager.getIsNetworkAvailable()) {
                true -> "Are you sure you want to delete this stop?"
                false -> "You are offline. You can't delete this stop."
              })
        },
        confirmButton = {
          TextButton(
              modifier = Modifier.testTag("confirmDeleteButton" + stop.stopId),
              onClick = {
                if (SessionManager.getIsNetworkAvailable()) {
                  stopItemViewModel.deleteStop(stop.stopId)
                }
                showDeleteConfirmDialog = false
              }) {
                Text("Confirm", color = MaterialTheme.colorScheme.error)
              }
        },
        dismissButton = {
          TextButton(
              modifier = Modifier.testTag("cancelDeleteButton" + stop.stopId),
              onClick = { showDeleteConfirmDialog = false }) {
                Text("Cancel")
              }
        })
  }

  // the horizontal divider
  Box(
      modifier =
          Modifier.fillMaxWidth(), // This ensures the box takes the full width of its container
      contentAlignment = Alignment.Center // This will center the content inside the box
      ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(), // Customize this width as needed
            thickness = 1.dp,
            color = outlineVariantLight)
      }
  if (isStopPressed) { // Display the stop information dialog
    StopInfoDialog(stop = stop, closeDialogueAction = { isStopPressed = false })
  }
}

@Composable
fun ShowToast(message: String) {
  val context = LocalContext.current
  LaunchedEffect(message) { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
}
