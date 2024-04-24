package com.github.se.wanderpals.ui.screens.trip.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.github.se.wanderpals.ui.navigation.Route
import java.time.format.DateTimeFormatter

/**
 * Composable function to display a dialog containing information about a stop in a trip agenda.
 *
 * @param announcement The [Announcement] object containing information about the stop.
 * @param notificationsViewModel view model containing UI states.
 */
@Composable
fun AnnouncementInfoDialog(announcement: Announcement, notificationsViewModel: NotificationsViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = { notificationsViewModel.setAnnouncementItemPressState(false) },
    ) {
        if (showDeleteDialog) {
            AlertDialog(
                modifier = Modifier.testTag("deleteAnnouncementDialog"),
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete this announcement?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            notificationsViewModel.removeAnnouncement(announcement.announcementId)
                            notificationsViewModel.setAnnouncementItemPressState(false)
                            showDeleteDialog = false},
                        modifier = Modifier.testTag("confirmDeleteAnnouncementButton")) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                },
              )
        }
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 100.dp)
                .testTag("announcementDialog"),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Title
                Text(
                    text = announcement.title,
                    style = TextStyle(fontSize = 22.sp),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("announcementTitle")
                )
                // Date
                Text(
                    text = announcement.timestamp.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm")),
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("announcementDate")
                )
                // Sender
                Text(
                    text = "by ${announcement.userName}",
                    style = TextStyle(fontSize = 16.sp),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("announcementSender")
                )
                // Description
                OutlinedTextField(
                    value = announcement.description,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth().height(300.dp)
                        .testTag("announcementDescription"),
                    readOnly = true
                )
                if(SessionManager.isAdmin()){
                Button(
                    onClick = {
                        showDeleteDialog = true
                    },
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp,vertical = 20.dp)
                        .height(50.dp)
                        .align(Alignment.CenterHorizontally)
                        .testTag("deleteAnnouncementButton"),

                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEE1F9))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = Icons.Default.Delete.name,
                            tint = Color(0xFF000000),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Delete announcement",
                            style =
                            TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
                }
            }
        }
    }
}
