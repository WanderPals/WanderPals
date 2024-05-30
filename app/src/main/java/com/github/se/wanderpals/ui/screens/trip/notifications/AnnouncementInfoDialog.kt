package com.github.se.wanderpals.ui.screens.trip.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.service.SessionManager
import java.time.format.DateTimeFormatter

/**
 * Composable function for displaying an information dialog for an announcement.
 *
 * @param announcement The [Announcement] to display information about.
 * @param notificationsViewModel The view model to handle notifications.
 */
@Composable
fun AnnouncementInfoDialog(
    announcement: Announcement,
    notificationsViewModel: NotificationsViewModel
) {
  var showDeleteDialog by remember { mutableStateOf(false) }
  Dialog(
      onDismissRequest = { notificationsViewModel.setAnnouncementItemPressState(false) },
  ) {
    if (showDeleteDialog) {
      AlertDialog(
          modifier = Modifier.testTag("deleteAnnouncementDialog"),
          onDismissRequest = { showDeleteDialog = false },
          title = { Text("Confirm Deletion") },
          text = {
            Text(
                when (SessionManager.getIsNetworkAvailable()) {
                  true -> "Are you sure you want to delete this announcement?"
                  false -> "Network is not available. Please try again later."
                })
          },
          confirmButton = {
            TextButton(
                onClick = {
                  if (SessionManager.getIsNetworkAvailable()) {
                    notificationsViewModel.removeAnnouncement(announcement.announcementId)
                    notificationsViewModel.setAnnouncementItemPressState(false)
                  }
                  showDeleteDialog = false
                },
                modifier = Modifier.testTag("confirmDeleteAnnouncementButton")) {
                  Text("Confirm")
                }
          },
          dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } },
      )
    }
    BoxWithConstraints {
      val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() * 0.8f }
      Surface(
          modifier = Modifier.fillMaxWidth().height(maxHeight).testTag("announcementDialog"),
          color = MaterialTheme.colorScheme.surface,
          shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {

                  // Title
                  Text(
                      text = announcement.title,
                      style = TextStyle(fontSize = 22.sp),
                      textAlign = TextAlign.Start,
                      modifier = Modifier.fillMaxWidth().testTag("announcementTitle"),
                      color = MaterialTheme.colorScheme.primary)
                  // Date
                  Text(
                      text =
                          announcement.timestamp.format(
                              DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm")),
                      style = TextStyle(fontSize = 16.sp),
                      textAlign = TextAlign.Start,
                      modifier = Modifier.fillMaxWidth().testTag("announcementDate"),
                      color = MaterialTheme.colorScheme.tertiary)
                  // Sender
                  Text(
                      text = "by ${announcement.userName}",
                      style = TextStyle(fontSize = 16.sp),
                      textAlign = TextAlign.Start,
                      modifier = Modifier.fillMaxWidth().testTag("announcementSender"),
                      color = MaterialTheme.colorScheme.tertiary)
                  // Description
                  OutlinedTextField(
                      value = announcement.description,
                      onValueChange = {},
                      modifier =
                          Modifier.fillMaxWidth()
                              .weight(1f)
                              .testTag("announcementDescription")
                              .background(MaterialTheme.colorScheme.surfaceVariant),
                      readOnly = true)
                  Box(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(bottom = 10.dp)
                              .height(70.dp)
                              .align(Alignment.CenterHorizontally)) {
                        if (SessionManager.isAdmin()) {

                          // Delete announcement button
                          Button(
                              onClick = { showDeleteDialog = true },
                              modifier = Modifier.fillMaxSize().testTag("deleteAnnouncementButton"),
                              colors =
                                  ButtonDefaults.buttonColors(
                                      containerColor =
                                          MaterialTheme.colorScheme.primaryContainer)) {
                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                  Icon(
                                      imageVector = Icons.Default.Delete,
                                      contentDescription = Icons.Default.Delete.name,
                                      tint = Color(0xFF000000),
                                      modifier = Modifier.size(20.dp))
                                  Text(
                                      text = "Delete announcement",
                                      style =
                                          TextStyle(
                                              fontSize = 14.sp,
                                              fontWeight = FontWeight(500),
                                              color = Color(0xFF000000),
                                              textAlign = TextAlign.Center,
                                          ))
                                }
                              }
                        }
                      }
                }
          }
    }
  }
}
