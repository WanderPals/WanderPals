package com.github.se.wanderpals.ui.screens.notification

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.viewmodel.CreateNotificationViewModel
import java.time.LocalDateTime

/**
 * CreateNotification composable responsible for adding a notification.
 *
 * @param tripId the id of the trip for which the notification is being created
 * @param viewModel a CreateNotificationViewModel that needs to be initialized beforehand
 * @param onSuccess code to execute after the successful creation of the notification
 * @param onFailure code to execute if the creation of the notification fails
 * @param onCancel code to execute if the user cancels the creation of the notification
 */
@Composable
fun CreateNotification(
    tripId: String,
    viewModel: CreateNotificationViewModel,
    //    title: String = "",
    //    description: String = "",
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {},
    onCancel: () -> Unit = {}
) {

  val MAX_NOTIF_TITLE_LENGTH = 55 // limit the trip notification title to 55 characters

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var titleError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }

  Surface(modifier = Modifier.padding(12.dp)) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      OutlinedButton(
          modifier = Modifier.align(Alignment.Start).testTag("tripNotifGoBackButton"),
          onClick = { onCancel() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
            )
          }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          OutlinedTextField(
              value = title,
              onValueChange = { if (it.length <= MAX_NOTIF_TITLE_LENGTH) title = it },
              label = { Text("Trip Notification Title*") },
              modifier = Modifier.fillMaxWidth().testTag("inputNotificationTitle"),
              isError = titleError,
              singleLine = true)
          Text(
              text = "${title.length}/$MAX_NOTIF_TITLE_LENGTH",
              modifier =
                  Modifier.align(Alignment.End).padding(end = 8.dp).testTag("notifTitleLengthText"),
              style = TextStyle(fontSize = 12.sp, color = Color.Gray))

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Trip Notification Description*") },
              modifier =
                  Modifier.fillMaxWidth().height(150.dp).testTag("inputNotificationDescription"),
              isError = descriptionError,
              singleLine = false)

          Spacer(modifier = Modifier.height(24.dp))

          Button(
              modifier = Modifier.fillMaxWidth().height(50.dp).testTag("createNotificationButton"),
              onClick = {
                titleError =
                    title.trim().isEmpty() // add trim() to remove leading and trailing whitespaces
                descriptionError = description.trim().isEmpty()

                if (!titleError && !descriptionError) {
                  // Logic to create notification
                  val tripNotification =
                      TripNotification(
                          notificationId = "", // modified by database
                          userId = "", // modified by database
                          title = title,
                          userName = "", // modified by database
                          description = description,
                          timestamp = LocalDateTime.now())
                  // if successful, call onSuccess(), otherwise onFailure()
                  if (viewModel.addNotification(tripId, tripNotification)) {
                    onSuccess()
                  } else {
                    onFailure()
                  }
                }
              }) {
                Text("Announce", fontSize = 24.sp)
              }
        }
  }
}

/*
@Preview(showBackground = true)
@Composable
fun PreviewCreateNotification() {
    val fakeViewModel = CreateNotificationViewModel() // Ensure this has a default constructor or provide necessary initializations
    CreateNotification(
        tripId = "123",
        viewModel = fakeViewModel,
        onSuccess = { /* Preview success */ },
        onFailure = { /* Preview failure */ },
        onCancel = { /* Preview cancel */ }
    )
}
*/
