package com.github.se.wanderpals.ui.screens.trip.notifications

import android.app.Notification
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.wanderpals.R
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.Expense
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.navigationActions
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.PullToRefreshLazyColumn
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.trip.finance.FinanceOption

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

  val suggestion by notificationsViewModel.currentSuggestion.collectAsState()
  val isLoadingSuggestion by notificationsViewModel.isSuggestionReady.collectAsState()

  val expense by notificationsViewModel.currentExpense.collectAsState()
  val isLoadingExpense by notificationsViewModel.isExpenseReady.collectAsState()

  LaunchedEffect(isLoadingSuggestion, isLoadingExpense) {
    if (isLoadingSuggestion) {
      navigationActions.variables.currentSuggestion = suggestion as Suggestion
      navigationActions.navigateTo(Route.SUGGESTION_DETAIL)
      notificationsViewModel.resetIsLoadingSuggestion()
    }
    if (isLoadingExpense) {
      navigationActions.variables.expense = expense as Expense
      navigationActions.navigateTo(Route.EXPENSE_INFO)
      notificationsViewModel.resetIsLoadingExpense()
    }
  }

    Scaffold (
        topBar = {
            NotificationTopBar(notificationSelected) {
                notificationsViewModel.setNotificationSelectionState(it)
            }
        }
            ) {
        Column(modifier = Modifier
            .padding(it)
            .testTag("notificationScreen")) {
            if (announcementItemPressed) {
                val selectedAnnouncement =
                    announcementList.find { announcement ->
                        announcement.announcementId == selectedAnnouncementId
                    }!!
                AnnouncementInfoDialog(
                    announcement = selectedAnnouncement, notificationsViewModel = notificationsViewModel)
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth())

            val itemsList = if (notificationSelected) notificationsList else announcementList

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center))
                }
            } else {
                if (itemsList.isEmpty()) {
                    val emptyItemText = if (notificationSelected) "notifications" else "announcements"
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp)) {
                        // text if no items found
                        Text(
                            text =
                            when {
                                SessionManager.getIsNetworkAvailable() ->
                                    "Looks like there is no $emptyItemText."
                                else -> "Looks like you are offline. Please check your network connection."
                            },
                            modifier =
                            Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 16.dp)
                                .testTag("noItemsText"),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge)
                        IconButton(
                            enabled = SessionManager.getIsNetworkAvailable(),
                            onClick = { notificationsViewModel.updateStateLists() },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 60.dp),
                            content = {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh notification")
                            })
                    }
                } else {
                    val lazyColumn =
                        @Composable {
                            LazyColumn(modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)) {
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
                                                            when (item.route) {
                                                                Route.SUGGESTION_DETAIL ->
                                                                    notificationsViewModel.getSuggestion(
                                                                        navigationActions.variables.suggestionId)
                                                                Route.EXPENSE_INFO ->
                                                                    notificationsViewModel.getExpense(
                                                                        navigationActions.variables.expense.expenseId)
                                                                Route.MAP -> navigationActions.navigateTo(Route.MAP)
                                                            }
                                                        } else {
                                                            navigationActions.navigateTo(item.route)
                                                        }
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
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        thickness = 1.dp,
                                        modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    PullToRefreshLazyColumn(
                        inputLazyColumn = lazyColumn,
                        onRefresh = { notificationsViewModel.updateStateLists() },
                        modifier = Modifier.weight(1f))
                }
            }

            // Create announcement Button
            if (!notificationSelected && SessionManager.isAdmin()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)) {
                    Button(
                        enabled = SessionManager.getIsNetworkAvailable(),
                        onClick = { navigationActions.navigateTo(Route.CREATE_ANNOUNCEMENT) },
                        modifier =
                        Modifier
                            .padding(horizontal = 20.dp)
                            .height(50.dp)
                            .align(Alignment.Center)
                            .testTag("createAnnouncementButton"),
                        colors =
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = Icons.Default.Add.name,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp))
                            Text(
                                text = "Make an announcement",
                                style =
                                TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(500),
                                    color = MaterialTheme.colorScheme.onPrimary),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
    }
  }
}


/**
 * Composable function for displaying the notification top bar.
 *
 * @param isNotificationSelected The currently selected notification screen.
 * @param onSelectOption Callback function for selecting a notification screen.
 */
@Composable
fun NotificationTopBar(
    isNotificationSelected: Boolean,
    onSelectOption: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.testTag("notificationTopBar"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceTint)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(start = 20.dp, top = 12.dp).height(35.dp),
                    text = "Notifications",
                    textAlign = TextAlign.Center,
                    style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    color = MaterialTheme.colorScheme.onPrimary)

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)){
                NavigationButton(
                    text = "Notifications",
                    isSelected = isNotificationSelected,
                    onClick = { onSelectOption(true) },
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                NavigationButton(
                    text = "Announcements",
                    isSelected = !isNotificationSelected,
                    onClick = { onSelectOption(false) })
            }
        }
    }
}

/**
 * Composable function for displaying a navigation button.
 *
 * @param text The text to display on the button.
 * @param isSelected Whether the button is currently selected.
 * @param onClick Callback function for when the button is clicked.
 */
@Composable
fun NavigationButton(text: String, isSelected: Boolean, onClick: () -> Unit) {

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(top = 12.dp)
            .testTag(text + "Button")
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        val colorSelection =
            if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = colorSelection,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier =
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                ),
        ) {}
    }
}
