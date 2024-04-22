package com.github.se.wanderpals.ui.screens.trip.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.wanderpals.model.data.Announcement
import java.time.format.DateTimeFormatter

/**
 * Composable function to display a dialog containing information about a stop in a trip agenda.
 *
 * @param announcement The [Announcement] object containing information about the stop.
 * @param closeDialogueAction Callback function to be invoked when the dialog is dismissed.
 */
@Composable
fun AnnouncementInfoDialog(announcement: Announcement, closeDialogueAction: () -> Unit) {
    Dialog(onDismissRequest = { closeDialogueAction() },
    ) {
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
                    style = TextStyle(fontSize = 24.sp),
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
                        .fillMaxWidth().weight(1f).padding(bottom = 125.dp)

                        .testTag("announcementDescription"),
                    readOnly = true
                )
            }
        }
    }
}
