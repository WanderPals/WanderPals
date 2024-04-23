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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.TripNotification
import java.time.format.DateTimeFormatter

/**
 * Composable function representing an item in the notification list.
 *
 * This function displays a single notification item with its title and timestamp.
 *
 * @param notification The notification to display.
 */
@Composable
fun NotificationItem(notification: TripNotification,onNotificationItemClick : () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)) {
        Button(
            onClick = {onNotificationItemClick()},
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
            Row(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterVertically)) {
                // Title Text
                Text(
                    text = notification.title,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
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
                        text = notification.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                    // Date: day
                    Text(
                        text =
                        notification.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                }
            }
        }
    }
}

/**
 * Composable function representing an item in the announcement list.
 *
 * This function displays a single announcement item with its title, timestamp, and username.
 *
 * @param announcement The announcement to display.
 */
@Composable
fun AnnouncementItem(announcement: Announcement,onAnnouncementItemClick : (String) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)) {
        Button(
            onClick = {onAnnouncementItemClick(announcement.announcementId)},
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
            Row(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterVertically)) {
                // Title Text
                Text(
                    text = announcement.title,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
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