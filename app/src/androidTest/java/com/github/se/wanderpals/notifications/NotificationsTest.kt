package com.github.se.wanderpals.notifications

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.Announcement
import com.github.se.wanderpals.model.data.Role
import com.github.se.wanderpals.model.data.TripNotification
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.screens.NotificationScreen
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.trip.notifications.Notification
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

val notification1 =
    TripNotification(
        title = "Username1 joined the trip",
        route = Route.ADMIN_PAGE,
        timestamp = LocalDateTime.now())

val notification2 =
    TripNotification(
        title = "A new suggestion has been created",
        route = "",
        timestamp = LocalDateTime.now())

val notification3 =
    TripNotification(
        title = "A new stop has been added",
        route = Route.STOPS_LIST,
        timestamp = LocalDateTime.now())

val notification4 =
    TripNotification(
        title = "A new suggestion has been created",
        route = Route.STOPS_LIST,
        timestamp = LocalDateTime.now())

val announcement1 =
    Announcement(
        announcementId = "1", // Replace with actual announcement ID
        userId = "1", // Replace with actual user ID
        title = "Username1 posted an announcement",
        userName = "John Doe", // Replace with actual user name
        description = "This is a new announcement!",
        timestamp = LocalDateTime.now() // Replace with actual timestamp
        )
val announcement2 =
    Announcement(
        announcementId = "2", // Replace with actual announcement ID
        userId = "2", // Replace with actual user ID
        title = "Username2 posted an announcement",
        userName = "Jane", // Replace with actual user name
        description = "This is a second announcement!",
        timestamp = LocalDateTime.now() // Replace with actual timestamp
        )

class NotificationsViewModelTest :
    NotificationsViewModel(TripsRepository("-1", dispatcher = Dispatchers.IO), "-1") {

  private val _notifStateList = MutableStateFlow(listOf(notification1, notification2,notification3,notification4))
  override val notifStateList: StateFlow<List<TripNotification>> = _notifStateList

  private val _announcementStateList = MutableStateFlow(listOf(announcement1))
  override val announcementStateList: StateFlow<List<Announcement>> = _announcementStateList

  private val _isLoading = MutableStateFlow(false)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  override fun updateStateLists() {
    _notifStateList.value = listOf(notification1, notification2)
    _announcementStateList.value = listOf(announcement1)
  }

  override fun addAnnouncement(announcement: Announcement) {
    _announcementStateList.value =
        _announcementStateList.value.toMutableList().apply { add(announcement2) }
  }

  override fun removeAnnouncement(announcementId: String) {
    _announcementStateList.value =
        _announcementStateList.value.toMutableList().apply {
          removeIf { it.announcementId == announcementId }
        }
  }
}

@RunWith(AndroidJUnit4::class)
class NotificationsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val notificationsViewModelTest = NotificationsViewModelTest()

  @Before
  fun testSetup() {
    SessionManager.setUserSession()
    SessionManager.setRole(Role.OWNER)
    composeTestRule.setContent {
      Notification(
          notificationsViewModel = notificationsViewModelTest, navigationActions = mockNavActions)
    }
  }

  @Test
  fun notifJoinTripNavigatesToAdmin() = run {
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      notifItemButtonWithPath {
        assertIsDisplayed()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.ADMIN_PAGE) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun notifItemWithNoPathDoesNothing() = run {
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      notifItemButtonWithoutPath {
        assertIsDisplayed()
        performClick()
      }
      verify(exactly = 0) { mockNavActions.navigateTo(any()) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun createAnnouncementButtonIsNotDisplayedIfUserHasNotPermission() = run {
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      notificationButton { assertIsDisplayed() }
      announcementButton { assertIsDisplayed() }
      announcementButton { performClick() }
      SessionManager.setRole(Role.VIEWER)
      createAnnouncementButton { assertIsNotDisplayed() }
      SessionManager.setRole(Role.MEMBER)
      createAnnouncementButton { assertIsNotDisplayed() }
    }
  }

  @Test
  fun viewerOrMemberCanReadAnnouncementInfoOnClick() = run {
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      SessionManager.setRole(Role.VIEWER)
      notificationButton { assertIsDisplayed() }
      announcementButton { assertIsDisplayed() }
      announcementButton { performClick() }
      announcementItemButton1 { performClick() }
      announcementDialog { assertIsDisplayed() }
      deleteAnnouncementButton { assertIsNotDisplayed() }
      deleteAnnouncementButton { assertIsNotDisplayed() }
    }
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      SessionManager.setRole(Role.MEMBER)
      notificationButton { assertIsDisplayed() }
      announcementButton { assertIsDisplayed() }
      announcementButton { performClick() }
      announcementItemButton1 { performClick() }
      announcementDialog { assertIsDisplayed() }
      deleteAnnouncementButton { assertIsNotDisplayed() }
      deleteAnnouncementButton { assertIsNotDisplayed() }
    }
  }

  @Test
  fun emptyTextIsDisplayedIfNoAnnouncementAreThere() = run {
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      notificationsViewModelTest.removeAnnouncement("1")
      notificationButton { assertIsDisplayed() }
      announcementButton { assertIsDisplayed() }
      announcementButton { performClick() }
      noItemsText { assertIsDisplayed() }
    }
  }

  @Test
  fun userWithPermissionsCanDeleteAnnouncement() = run {
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      announcementButton { performClick() }
      announcementItemButton1 { performClick() }
      announcementDialog { assertIsDisplayed() }
      deleteAnnouncementButton { performClick() }
      deleteAnnouncementDialog { assertIsDisplayed() }
      confirmDeleteAnnouncementButton { performClick() }
      noItemsText { assertIsDisplayed() }
    }
  }
}
