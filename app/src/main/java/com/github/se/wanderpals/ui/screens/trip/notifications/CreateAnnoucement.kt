package com.github.se.wanderpals.ui.screens.trip.notifications

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
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.viewmodel.CreateAnnouncementViewModel
import java.time.LocalDateTime

/**
 * CreateAnnouncement composable responsible for adding a Announcement.
 *
 * @param tripId the id of the trip for which the Announcement is being created
 * @param viewModel a CreateAnnouncementViewModel that needs to be initialized beforehand
 * @param onSuccess code to execute after the successful creation of the Announcement
 * @param onFailure code to execute if the creation of the Announcement fails
 * @param onCancel code to execute if the user cancels the creation of the Announcement
 */
@Composable
fun CreateAnnouncement(
    tripId: String,
    viewModel: CreateAnnouncementViewModel,
    onNavigationBack: () -> Unit =
        {}, // is either onSuccess or onCancel, because the user is navigating back to the previous
    // screen which is the same screen for both actions
) {

  val MAX_ANNOUNCEMENT_TITLE_LENGTH = 55 // limit the trip Announcement title to 55 characters

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var titleError by remember { mutableStateOf("") }
  var descriptionError by remember { mutableStateOf("") }

  Surface(modifier = Modifier.padding(12.dp)) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp), // reduced top padding
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      OutlinedButton(
          modifier = Modifier.align(Alignment.Start).testTag("tripAnnouncementGoBackButton"),
          onClick = { onNavigationBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
            )
          }
    }

    Spacer(modifier = Modifier.height(8.dp)) // Reduced space for a closer layout

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          OutlinedTextField(
              value = title,
              onValueChange = {
                if (it.length <= MAX_ANNOUNCEMENT_TITLE_LENGTH) {
                  title = it
                  // Reset titleError if the user starts typing again after an error was shown
                  titleError = if (title.trim().isEmpty()) "Title cannot be empty" else ""
                }
              },
              label = { Text("Trip Announcement Title*") },
              modifier = Modifier.fillMaxWidth().testTag("inputAnnouncementTitle"),
              isError =
                  titleError.isNotEmpty() &&
                      title
                          .trim()
                          .isEmpty(), // Only set isError to true if the title is actually empty, so
              // that the error message is shown and the field is
              // highlighted in red
              singleLine = true)

          Text(
              text = "${title.length}/$MAX_ANNOUNCEMENT_TITLE_LENGTH",
              modifier =
                  Modifier.align(Alignment.End)
                      .padding(end = 8.dp)
                      .testTag("announcementTitleLengthText"),
              style =
                  TextStyle(
                      fontSize = 12.sp,
                      color =
                          if (titleError.isNotEmpty() && title.trim().isEmpty()) Color.Red
                          else Color.Gray))
          if (titleError.isNotEmpty() && title.trim().isEmpty()) {
            // then display the error message
            Text(
                text = titleError,
                color = Color.Red,
                modifier = Modifier.align(Alignment.End).padding(end = 8.dp),
                style = TextStyle(fontSize = 12.sp))
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
              value = description,
              onValueChange = {
                description = it
                // Reset descriptionError if the user starts typing again after an error was shown
                descriptionError =
                    if (description.trim().isEmpty()) "Description cannot be empty" else ""
              },
              label = { Text("Trip Announcement Description*") },
              modifier =
                  Modifier.fillMaxWidth()
                      .height(450.dp) // the height for a the description field area
                      .testTag("inputAnnouncementDescription"),
              isError =
                  descriptionError.isNotEmpty() &&
                      description
                          .trim()
                          .isEmpty(), // Only set isError to true if the description is actually
              // empty so that the error message is shown and the field is
              // highlighted in red
              singleLine = false)
          if (descriptionError.isNotEmpty() && description.trim().isEmpty()) {
            Text(
                text = descriptionError,
                color = Color.Red,
                modifier = Modifier.align(Alignment.End).padding(end = 8.dp),
                style = TextStyle(fontSize = 12.sp))
          }

          Spacer(modifier = Modifier.height(32.dp))

          Button(
              modifier = Modifier.fillMaxWidth().height(50.dp).testTag("createAnnouncementButton"),
              onClick = {
                titleError = if (title.trim().isEmpty()) "Title cannot be empty" else ""
                // add trim() to remove leading and trailing whitespaces
                descriptionError =
                    if (description.trim().isEmpty()) "Description cannot be empty" else ""

                if (titleError.isEmpty() && descriptionError.isEmpty()) {
                  // Logic to create Announcement
                  val announcement =
                      Announcement(
                          announcementId = "", // modified by database
                          userId = "", // modified by database
                          title = title,
                          userName = "", // modified by database
                          description = description,
                          timestamp = LocalDateTime.now())
                  // if successful, call onSuccess(), otherwise onFailure()
                  if (viewModel.addAnnouncement(tripId, announcement)) {
                    onNavigationBack()
                  }
                }
              }) {
                Text("Announce", fontSize = 24.sp)
              }
        }
  }
}
