package com.github.se.wanderpals.ui.screens.overview

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import com.github.se.wanderpals.model.data.Trip
import com.github.se.wanderpals.model.viewmodel.OverviewViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
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
 * Enum class representing the state of the currently selected icon button in the UI.
 *
 * This enum is used to track which icon button is currently selected or pressed in the
 * `OverviewTrip` composable function. It helps manage the visual feedback and actions associated
 * with each button.
 */
private enum class SelectedIconButton {
  NONE,
  SEND,
  SHARE,
  DOCUMENT
}

/**
 * Composable function that represents an overview of a trip. Displays basic trip information such
 * as title, start date, and end date.
 *
 * @param trip The trip object containing trip details.
 * @param navigationActions The navigation actions used for navigating to detailed trip view.
 * @param overviewViewModel The view model containing the data and logic for the overview screen.
 * @param addShortcut A lambda function to add a shortcut to the home screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverviewTrip(
    trip: Trip,
    navigationActions: NavigationActions,
    overviewViewModel: OverviewViewModel,
    addShortcut: (Trip) -> Unit
) {

  // Date pattern for formatting start and end dates
  val datePattern = "dd/MM/yyyy"

  // Local context
  val context = LocalContext.current

  // Mutable state to check which button is selected
  val selectedIconButton = remember { mutableStateOf(SelectedIconButton.NONE) }

  // Mutable state to check if the dialog is open
  var dialogIsOpen by remember { mutableStateOf(false) }
  var dialogIsOpenEmail by remember { mutableStateOf(false) }

  var displayedTheBoxSelector by remember { mutableStateOf(false) }
  var selectedImagesLocal by remember { mutableStateOf<Uri?>(Uri.EMPTY) }

  val singlePhotoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri -> selectedImagesLocal = uri })

  // Use of a launch effect to reset the value of isSelected to false after 100ms
  LaunchedEffect(selectedIconButton.value) {
    if (selectedIconButton.value != SelectedIconButton.NONE) {
      delay(100)
      selectedIconButton.value = SelectedIconButton.NONE
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

  Card(
      modifier =
          Modifier.fillMaxSize()
              .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
              .combinedClickable(
                  onClick = {
                    if (trip.users.find { it == SessionManager.getCurrentUser()!!.userId } !=
                        null) {
                      dialogIsOpen = false
                      SessionManager.setTripName(trip.title)
                      navigationActions.setVariablesTrip(trip.tripId)
                      navigationActions.navigateTo(Route.TRIP)
                    } else {
                      dialogIsOpen = true
                    }
                  },
                  onLongClick = { addShortcut(trip) })
              .width(360.dp)
              .height(130.dp)
              .testTag("buttonTrip" + trip.tripId),
      shape = RoundedCornerShape(15.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
      colors =
          CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Box(modifier = Modifier.fillMaxSize()) {
          // Column containing trip information
          Column(
              modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    // Ensure padding for visual spacing
                ) {
                  // Trip title
                  Text(
                      text = trip.title,
                      modifier =
                          Modifier.fillMaxWidth()
                              .weight(1f)
                              .height(24.dp)
                              .testTag("tripTitle" + trip.tripId),
                      style =
                          TextStyle(
                              fontSize = 18.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                              letterSpacing = 0.1.sp,
                          ),
                      textAlign = TextAlign.Start,
                      overflow = TextOverflow.Ellipsis,
                      maxLines = 1)

                  Spacer(Modifier.weight(0.1f))

                  // Start date
                  Text(
                      text = trip.startDate.format(DateTimeFormatter.ofPattern(datePattern)),
                      modifier =
                          Modifier.height(24.dp)
                              .padding(top = 4.dp)
                              .testTag(
                                  "startDate" +
                                      trip.tripId), // the padding is for having the text on the
                      // same
                      // line and in the same height as the trip title
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                              letterSpacing = 0.1.sp,
                          ),
                      textAlign = TextAlign.End)

                  Spacer(modifier = Modifier.width(11.dp)) // Space between start and end date

                  // End date
                  Text(
                      text = trip.endDate.format(DateTimeFormatter.ofPattern(datePattern)),
                      modifier =
                          Modifier.height(24.dp)
                              .padding(top = 4.dp)
                              .testTag(
                                  "endDate" +
                                      trip.tripId), // the padding is for having the text on the
                      // same
                      // line and in the same height as the trip title
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                              letterSpacing = 0.1.sp,
                          ),
                      textAlign = TextAlign.End)
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier =
                        Modifier.fillMaxWidth() // Ensure padding for visual spacing
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                      Spacer(Modifier.weight(0.9f)) // Pushes the icon to the end

                      // Send trip code button
                      IconButton(
                          modifier =
                              Modifier.width(24.dp)
                                  .height(28.dp)
                                  .testTag("sendTripButton" + trip.tripId),
                          onClick = {
                            selectedIconButton.value = SelectedIconButton.SEND
                            dialogIsOpenEmail = true
                          }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier =
                                    Modifier.background(
                                        if (selectedIconButton.value == SelectedIconButton.SEND)
                                            MaterialTheme.colorScheme.surfaceDim
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
                            selectedIconButton.value = SelectedIconButton.SHARE
                            context.shareTripCodeIntent(trip.tripId)
                          }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier =
                                    Modifier.background(
                                        if (selectedIconButton.value == SelectedIconButton.SHARE)
                                            MaterialTheme.colorScheme.surfaceDim
                                        else Color.Transparent))
                          }
                      Spacer(Modifier.width(10.dp))
                      IconButton(
                          onClick = {
                            selectedIconButton.value = SelectedIconButton.DOCUMENT
                            displayedTheBoxSelector = true
                          },
                          modifier =
                              Modifier.width(24.dp).height(28.dp).testTag("documentButton")) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier =
                                    Modifier.background(
                                        if (selectedIconButton.value == SelectedIconButton.DOCUMENT)
                                            MaterialTheme.colorScheme.surfaceDim
                                        else Color.Transparent))
                          }
                    }
              }
        }
      }
  if (displayedTheBoxSelector) {
    Dialog(onDismissRequest = { displayedTheBoxSelector = false }) {
      Card(
          colors = CardDefaults.cardColors(contentColor = Color.Black),
          modifier = Modifier.size(370.dp, 300.dp).testTag("documentBox")) {

            // set the name of the documents
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

                  // Button to add the document from the media picker
                  FloatingActionButton(
                      onClick = { singlePhotoPickerLauncher.launch(PickVisualMediaRequest()) },
                      modifier =
                          Modifier.padding(top = 20.dp)
                              .size(width = 200.dp, height = 50.dp)
                              .testTag("addDocumentButton")) {
                        Text(
                            text = "Add Document",
                            style =
                                TextStyle(
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 0.5.sp,
                                ),
                        )
                      }
                  // button to accept the document
                  Row(
                      modifier = Modifier.padding(top = 10.dp),
                      horizontalArrangement = Arrangement.SpaceEvenly) {
                        FloatingActionButton(
                            onClick = {
                              if (selectedImagesLocal != Uri.EMPTY) {
                                overviewViewModel.addDocument(
                                    trip.tripId,
                                    selectedImagesLocal!!,
                                    "trip",
                                    context,
                                    Firebase.storage.reference)
                              }
                              selectedImagesLocal = Uri.EMPTY
                              displayedTheBoxSelector = false
                            },
                            modifier =
                                Modifier.padding(top = 10.dp)
                                    .size(width = 100.dp, height = 50.dp)
                                    .testTag("acceptButton")) {
                              Text(
                                  text = "Accept",
                                  style =
                                      TextStyle(
                                          fontSize = 16.sp,
                                          textAlign = TextAlign.Center,
                                          letterSpacing = 0.5.sp,
                                      ),
                              )
                            }
                        Spacer(modifier = Modifier.width(50.dp))
                        // cancel button
                        FloatingActionButton(
                            onClick = {
                              selectedImagesLocal = Uri.EMPTY
                              displayedTheBoxSelector = false
                            },
                            modifier =
                                Modifier.padding(top = 10.dp)
                                    .size(width = 100.dp, height = 50.dp)
                                    .testTag("cancelButton")) {
                              Text(
                                  text = "Cancel",
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
