package com.github.se.wanderpals.trip

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.screens.TripScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.rememberMultiNavigationAppState
import com.github.se.wanderpals.ui.screens.trip.Trip
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TripMenuTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavController: NavHostController

  @RelaxedMockK lateinit var mockNavController2: NavHostController

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      mockNavController = rememberNavController()
      mockNavController2 = rememberNavController()
      mockNavActions =
          NavigationActions(
              mainNavigation =
                  rememberMultiNavigationAppState(
                      startDestination = Route.ROOT_ROUTE, mockNavController),
              tripNavigation =
                  rememberMultiNavigationAppState(
                      startDestination = Route.DASHBOARD, mockNavController2))
      Trip(mockNavActions, "id", TripsRepository("-1", dispatcher = Dispatchers.IO), null)
    }
  }

  @Test
  fun checkViews() = run {
    ComposeScreen.onComposeScreen<TripScreen>(composeTestRule) {
      menuButton { assertIsDisplayed() }
      dashboardItem { assertIsDisplayed() }
    }
  }

  @Test
  fun startingClosedMenu() = run {
    ComposeScreen.onComposeScreen<TripScreen>(composeTestRule) {
      menuButton { assertIsDisplayed() }
      menuNav { assertIsNotDisplayed() }
    }
  }

  @Test
  fun openMenu() = run {
    ComposeScreen.onComposeScreen<TripScreen>(composeTestRule) {
      menuButton { performClick() }
      menuNav { assertIsDisplayed() }
    }
  }
}
