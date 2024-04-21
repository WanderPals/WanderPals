package com.github.se.wanderpals.notifications

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.NotificationsViewModel
import com.github.se.wanderpals.screens.NotificationScreen
import com.github.se.wanderpals.ui.screens.trip.notifications.Notification
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      Notification(
          notificationsViewModel =
              NotificationsViewModel(TripsRepository("-1", dispatcher = Dispatchers.IO)))
    }
  }

  @Test
  fun mainComponentAreDisplayed() = run {
    ComposeScreen.onComposeScreen<NotificationScreen>(composeTestRule) {
      notificationButton { assertIsDisplayed() }
      announcementButton { assertIsDisplayed() }
      createAnnouncementButton { assertIsNotDisplayed() }
      announcementButton { performClick() }
      createAnnouncementButton { assertIsDisplayed() }
    }
  }
}
