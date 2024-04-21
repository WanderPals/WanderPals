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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import java.time.format.DateTimeFormatter

/**
 * Composable function representing the Notification screen.
 *
 * This function displays notifications or announcements based on user selection.
 *
 * @param notificationsViewModel The view model containing notifications data.
 */
@Composable
fun Notification(notificationsViewModel: NotificationsViewModel) {

    val notificationsList by notificationsViewModel.notifStateList.collectAsState()
    val announcementList by notificationsViewModel.announcementStateList.collectAsState()
    var notificationSelected by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier.testTag("notificationScreen")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp, vertical = 22.dp),
            shape = RoundedCornerShape(70.dp),
            color = Color(0xFFA5B2C2)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .testTag("notificationButton"),
                    onClick = { notificationSelected = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (notificationSelected) Color(0xFF5A7BF0) else Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (notificationSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .testTag("announcementButton"),
                    onClick = { notificationSelected = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!notificationSelected) Color(0xFF5A7BF0) else Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Announcement",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (!notificationSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = Color.Black, thickness = 2.dp, modifier = Modifier.fillMaxWidth())
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            val itemsList = if (notificationSelected) notificationsList else announcementList
            items(itemsList) { item ->
                when (item) {
                    is TripNotification -> {
                        NotificationItem(notification = item)
                    }
                    is Announcement -> {
                        AnnouncementItem(announcement = item)
                    }
                }

                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        HorizontalDivider(color = Color.Black, thickness = 2.dp, modifier = Modifier.fillMaxWidth())
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)) {
            if (!notificationSelected) {
                Button(
                    onClick = { },
                    modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .height(50.dp)
                        .align(Alignment.Center)
                        .testTag("createAnnouncementButton"),
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
                            imageVector = Icons.Default.Add,
                            contentDescription = Icons.Default.Add.name,
                            tint = Color(0xFF000000),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Make an announcement",
                            style =
                            TextStyle(
                                fontSize = 18.sp,
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

/**
 * Composable function representing an item in the notification list.
 *
 * This function displays a single notification item with its title and timestamp.
 *
 * @param notification The notification to display.
 */
@Composable
fun NotificationItem(notification: TripNotification) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Button(
            onClick = { },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterVertically)
            ) {
                // Title Text
                Text(
                    text = notification.title,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Start
                )

                // Spacer
                Spacer(modifier = Modifier.width(16.dp))

                // Texts Column
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.End
                ) {

                    // Date: hour
                    Text(
                        text = notification.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                    // Date: day
                    Text(
                        text = notification.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
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
 * @param notification The announcement to display.
 */
@Composable
fun AnnouncementItem(announcement: Announcement) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Button(
            onClick = { },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterVertically)
            ) {
                // Title Text
                Text(
                    text = announcement.title,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Start
                )

                // Spacer
                Spacer(modifier = Modifier.width(16.dp))

                // Texts Column
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.End
                ) {

                    // Date: hour
                    Text(
                        text = announcement.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                    // Date: day
                    Text(
                        text = announcement.timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )

                    // Username
                    Text(
                        text = announcement.userName,
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                }

            }
        }
    }
}