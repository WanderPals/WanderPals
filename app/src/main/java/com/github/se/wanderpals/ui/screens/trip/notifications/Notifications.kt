package com.github.se.wanderpals.ui.screens.trip.notifications

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.overview.shareTripCodeIntent
import java.time.format.DateTimeFormatter

/** The Notification screen. */
@Composable
fun Notification(notificationsViewModel: NotificationsViewModel) {

    val notificationsList by notificationsViewModel.notifStateList.collectAsState()

    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Notifications")
        }
        HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(notificationsList) { notification ->
                if(notification == notificationsList.first()){
                    HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                }
                NotificationItem(notification = notification)
                HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

            }
        }
    }
}

@Composable
fun NotificationItem(notification : TripNotification){
    Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
        Button(
            onClick = { },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
            Row(modifier = Modifier.fillMaxSize().align(Alignment.CenterVertically)) {
                // Title Text
                Text(
                    text = notification.title,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.Black,
                    modifier = Modifier.weight(1f).fillMaxWidth().align(Alignment.CenterVertically),
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

                    // Username
                    Text(
                        text = notification.userName,
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                }

            }
        }
    }
}
