package com.github.se.wanderpals.ui.screens.overview

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
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

/**
 * Send the trip code using an intent by email
 *
 * Creates an intent to send the trip code by email
 *
 * @param tripId The trip code to be shared.
 */
fun Context.sendTripCodeIntent(tripId: String, address: String) {

  val body = "You have been invited to join a trip. Use the following code to join: $tripId"

  val sendIntent =
      Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        putExtra(Intent.EXTRA_SUBJECT, "WanderPals Trip Invitation")
        putExtra(Intent.EXTRA_TEXT, body)
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
fun OverviewTrip(
    trip: Trip,
    navigationActions: NavigationActions,
    overviewViewModel: OverviewViewModel
) {

  // Date pattern for formatting start and end dates
  val DATE_PATTERN = "dd/MM/yyyy"

  // Local context
  val context = LocalContext.current

  // Mutable state to check if the icon button for sharing the trip is selected
  val isSelected = remember { mutableStateOf(false) }
  val isEmailSelected = remember { mutableStateOf(false) }

  // Mutable state to check if the dialog is open
  var dialogIsOpen by remember { mutableStateOf(false) }
  var dialogIsOpenEmail by remember { mutableStateOf(false) }

  // Use of a launch effect to reset the value of isSelected to false after 100ms
  LaunchedEffect(isSelected.value) {
    if (isSelected.value) {
      delay(100)
      isSelected.value = false
    }
  }

  LaunchedEffect(isEmailSelected.value) {
    if (isEmailSelected.value) {
      delay(100)
      isEmailSelected.value = false
    }
  }

  if (dialogIsOpenEmail) {
    DialogHandlerEmail(
        closeDialogueAction = { dialogIsOpenEmail = false },
        processMail = {
          context.sendTripCodeIntent(trip.tripId, overviewViewModel.userToSend.value)
        },
        overviewViewModel = overviewViewModel)
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
                      textAlign = TextAlign.Start,
                      overflow = TextOverflow.Ellipsis,
                      maxLines = 1)

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
                              .testTag("sendTripButton" + trip.tripId),
                      onClick = {
                        isEmailSelected.value = true
                        dialogIsOpenEmail = true
                      }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier =
                                Modifier.background(
                                    if (isEmailSelected.value) Color.LightGray
                                    else Color.Transparent))
                      }

                  Spacer(Modifier.width(10.dp))

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
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier =
                                Modifier.background(
                                    if (isSelected.value) Color.LightGray else Color.Transparent))
                      }
                }
          }
          AsyncImage(model = trip.imageUrl, contentDescription = "Image of the trip")
        }
  }
}

@Composable
fun DialogHandlerEmail(
    closeDialogueAction: () -> Unit,
    processMail: () -> Unit,
    overviewViewModel: OverviewViewModel
) {

  // Mutable state to hold the email input
  var username by remember { mutableStateOf("") }
  val canSend by overviewViewModel.canSend.collectAsState()

  // Dialog composable
  Dialog(
      onDismissRequest = {
        overviewViewModel.clearUserToSend()
        closeDialogueAction()
      }) {
        Surface(
            modifier = Modifier.height(220.dp).testTag("emailDialog"),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)) {
              Column(
                  modifier = Modifier.padding(16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = username,
                        onValueChange = { username = it },
                        label = {
                          Text(
                              text = "Insert the username",
                              style =
                                  TextStyle(
                                      fontSize = 18.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        },
                        singleLine = true)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Button to add user
                    Button(
                        onClick = { overviewViewModel.addUserToSend(username) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)) {
                          Text(
                              text = "Add user",
                              style =
                                  TextStyle(
                                      fontSize = 16.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        }

                    // Button to send email
                    Button(
                        onClick = { processMail() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = canSend) {
                          Text(
                              text = "Send email",
                              style =
                                  TextStyle(
                                      fontSize = 16.sp,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ),
                          )
                        }
                  }
            }
      }
}
