package com.github.se.wanderpals.ui.screens.trip.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.TripNotification
import java.time.format.DateTimeFormatter

/**
 * Composable function for displaying a notification item.
 *
 * @param notification The [TripNotification] to display.
 * @param onNotificationItemClick Callback function to handle click events on the notification item.
 */
@Composable
fun NotificationItem(notification: TripNotification, onNotificationItemClick: () -> Unit) {
  Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
    Button(
        modifier = Modifier.testTag("notifItemButton" + notification.route),
        onClick = { onNotificationItemClick() },
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
          Row(modifier = Modifier.fillMaxSize().align(Alignment.CenterVertically)) {
            // Title Text
            Text(
                text = notification.title,
                style = TextStyle(fontSize = 16.sp),
                color = if (notification.route.isNotEmpty()) Color.Black else Color.Gray,
                modifier = Modifier.weight(1f).fillMaxWidth().align(Alignment.CenterVertically),
                textAlign = TextAlign.Start,
                maxLines = 2)

            // Spacer
            Spacer(modifier = Modifier.width(16.dp))

            // Texts Column
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End) {

                  // Date: hour
                  Text(
                      text = notification.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                      style = TextStyle(fontSize = 14.sp, color = Color.Gray))
                  // Date: day
                  Text(
                      text =
                          notification.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                      style = TextStyle(fontSize = 14.sp, color = Color.Gray))
                }
          }
        }
  }
}

/**
 * Composable function for displaying an announcement item.
 *
 * @param announcement The [Announcement] to display.
 * @param onAnnouncementItemClick Callback function to handle click events on the announcement item.
 */
@Composable
fun AnnouncementItem(announcement: Announcement, onAnnouncementItemClick: (String) -> Unit) {
  Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
    Button(
        modifier = Modifier.testTag("announcementItemButton" + announcement.announcementId),
        onClick = { onAnnouncementItemClick(announcement.announcementId) },
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
          Row(modifier = Modifier.fillMaxSize().align(Alignment.CenterVertically)) {
            // Title Text
            Text(
                text = announcement.title,
                style = TextStyle(fontSize = 16.sp),
                color = Color.Black,
                modifier = Modifier.weight(1f).fillMaxWidth().align(Alignment.CenterVertically),
                textAlign = TextAlign.Start)

            // Spacer
            Spacer(modifier = Modifier.width(16.dp))

            // Texts Column
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End) {

                  // Date: hour
                  Text(
                      text = announcement.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                      style = TextStyle(fontSize = 14.sp, color = Color.Gray))
                  // Date: day
                  Text(
                      text =
                          announcement.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                      style = TextStyle(fontSize = 14.sp, color = Color.Gray))

                  // Username
                  Text(
                      text = announcement.userName,
                      style = TextStyle(fontSize = 14.sp, color = Color.Gray))
                }
          }
        }
  }
}
