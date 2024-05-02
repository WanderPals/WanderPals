package com.github.se.wanderpals.ui.screens.trip.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route

/**
 * Composable function for displaying notifications and announcements.
 *
 * This composable-function displays a list of notifications and announcements. It allows users to
 * switch between switch between viewing notifications and viewing announcements. Users can click on
 * notifications to navigate to specific destinations within the app. Admin/owner users have the
 * ability to create new announcements and delete announcements.
 *
 * @param notificationsViewModel The view model used to manage notifications and announcements.
 * @param navigationActions Actions for navigating to different destinations within the app.
 */
@Composable
fun Notification(
    notificationsViewModel: NotificationsViewModel,
    navigationActions: NavigationActions
) {

  LaunchedEffect(
      Unit) { // This ensures updateStateLists is called once per composition, not on every
        // recomposition
        notificationsViewModel.updateStateLists()
      }

  // UI states
  val notificationsList by notificationsViewModel.notifStateList.collectAsState()
  val announcementList by notificationsViewModel.announcementStateList.collectAsState()

  val notificationSelected by notificationsViewModel.isNotifSelected.collectAsState()
  val announcementItemPressed by notificationsViewModel.announcementItemPressed.collectAsState()

  val selectedAnnouncementId by notificationsViewModel.selectedAnnouncementID.collectAsState()

  val isLoading by notificationsViewModel.isLoading.collectAsState()

  Column(modifier = Modifier.testTag("notificationScreen")) {
    if (announcementItemPressed) {
      val selectedAnnouncement =
          announcementList.find { announcement ->
            announcement.announcementId == selectedAnnouncementId
          }!!
      AnnouncementInfoDialog(
          announcement = selectedAnnouncement, notificationsViewModel = notificationsViewModel)
    }
    // TOP menu
    Surface(
        modifier =
            Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 16.dp, vertical = 22.dp),
        shape = RoundedCornerShape(70.dp),
        color = Color(0xFFA5B2C2)) {
          Row(
              modifier = Modifier.fillMaxSize(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically) {
                Button(
                    modifier = Modifier.fillMaxHeight().weight(1f).testTag("notificationButton"),
                    onClick = { notificationsViewModel.setNotificationSelectionState(true) },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (notificationSelected) Color(0xFF5A7BF0) else Color.Transparent,
                            contentColor = Color.White)) {
                      Text(
                          text = "Notifications",
                          style =
                              MaterialTheme.typography.bodyLarge.copy(
                                  fontWeight =
                                      if (notificationSelected) FontWeight.Bold
                                      else FontWeight.Normal))
                    }
                Button(
                    modifier = Modifier.fillMaxHeight().weight(1f).testTag("announcementButton"),
                    onClick = { notificationsViewModel.setNotificationSelectionState(false) },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (!notificationSelected) Color(0xFF5A7BF0) else Color.Transparent,
                            contentColor = Color.White)) {
                      Text(
                          text = "Announcements",
                          style =
                              MaterialTheme.typography.bodyLarge.copy(
                                  fontWeight =
                                      if (!notificationSelected) FontWeight.Bold
                                      else FontWeight.Normal))
                    }
              }
        }

    HorizontalDivider(color = Color.Black, thickness = 2.dp, modifier = Modifier.fillMaxWidth())

    val itemsList = if (notificationSelected) notificationsList else announcementList

    if (isLoading) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp).align(Alignment.Center))
      }
    } else {
      if (itemsList.isEmpty()) {
        val emptyItemText = if (notificationSelected) "notifications" else "announcements"
        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 16.dp)) {
          // text if no items found
          Text(
              text = "Looks like there is no $emptyItemText.",
              modifier =
                  Modifier.align(Alignment.Center)
                      .padding(horizontal = 16.dp)
                      .testTag("noItemsText"),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodyLarge)
          IconButton(
              onClick = { notificationsViewModel.updateStateLists() },
              modifier = Modifier.align(Alignment.Center).padding(top = 60.dp),
              content = {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh notification")
              })
        }
      } else {
        val lazyColumn =
            @Composable {
              LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                items(itemsList) { item ->
                  when (item) {
                    is TripNotification -> {
                      NotificationItem(
                          notification = item,
                          onNotificationItemClick = {
                            if (item.route.isNotEmpty()) {
                              if (item.navActionVariables.isNotEmpty()) {
                                navigationActions.deserializeNavigationVariables(
                                    item.navActionVariables)
                              }
                              navigationActions.navigateTo(item.route)
                            }
                          })
                    }
                    is Announcement -> {
                      AnnouncementItem(
                          announcement = item,
                          onAnnouncementItemClick = { announcementId ->
                            notificationsViewModel.setAnnouncementItemPressState(true)
                            notificationsViewModel.setSelectedAnnouncementId(announcementId)
                          })
                    }
                  }
                  HorizontalDivider(
                      color = Color.Gray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                }
              }
            }
        PullToRefreshLazyColumn(
            inputLazyColumn = lazyColumn, onRefresh = { notificationsViewModel.updateStateLists() })
      }
    }

    // Create announcement Button
    if (!notificationSelected && SessionManager.isAdmin()) {
      Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        Button(
            onClick = { navigationActions.navigateTo(Route.CREATE_ANNOUNCEMENT) },
            modifier =
                Modifier.padding(horizontal = 20.dp)
                    .height(50.dp)
                    .align(Alignment.Center)
                    .testTag("createAnnouncementButton"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEE1F9))) {
              Row(
                  horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Icons.Default.Add.name,
                    tint = Color(0xFF000000),
                    modifier = Modifier.size(20.dp))
                Text(
                    text = "Make an announcement",
                    style =
                        TextStyle(
                            fontSize = 18.sp,
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
