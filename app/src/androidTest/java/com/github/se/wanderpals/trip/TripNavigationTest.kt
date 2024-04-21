package com.github.se.wanderpals.trip

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.wanderpals.BuildConfig
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.screens.TripScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.rememberMultiNavigationAppState
import com.github.se.wanderpals.ui.screens.trip.Trip
import com.google.android.libraries.places.api.Places
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TripNavigationTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
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
      val context = InstrumentationRegistry.getInstrumentation().targetContext
      Places.initialize(context, BuildConfig.MAPS_API_KEY)
      val placesClient = Places.createClient(context)
      Trip(mockNavActions, "id", TripsRepository("-1", Dispatchers.IO), placesClient)
    }
  }

  @Test
  fun checkViews() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      bottomNav { assertIsDisplayed() }
      suggestionItem { assertIsDisplayed() }
      agendaItem { assertIsDisplayed() }
      dashboardItem { assertIsDisplayed() }
      mapItem { assertIsDisplayed() }
      notificationItem { assertIsDisplayed() }
    }
  }

  @Test
  fun startingOnDashboard() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      dashboardItem { assertIsSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToFinance() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      suggestionItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToAgenda() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      agendaItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToMap() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      mapItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToNotification() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      notificationItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsDisplayed() }
    }
  }

  @Test
  fun goingToDashboard() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      dashboardItem { performClick() }

      dashboardItem { assertIsSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToFinanceAndBack() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      suggestionItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }

    onComposeScreen<TripScreen>(composeTestRule) {
      dashboardItem { performClick() }

      dashboardItem { assertIsSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToAgendaAndBack() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      agendaItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }

    onComposeScreen<TripScreen>(composeTestRule) {
      dashboardItem { performClick() }

      dashboardItem { assertIsSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToMapAndBack() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      mapItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }

    onComposeScreen<TripScreen>(composeTestRule) {
      dashboardItem { performClick() }

      dashboardItem { assertIsSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }

  @Test
  fun goingToNotificationAndBack() = run {
    onComposeScreen<TripScreen>(composeTestRule) {
      notificationItem { performClick() }

      dashboardItem { assertIsNotSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsSelected() }

      dashboardScreen { assertIsNotDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsDisplayed() }
    }

    onComposeScreen<TripScreen>(composeTestRule) {
      dashboardItem { performClick() }

      dashboardItem { assertIsSelected() }
      suggestionItem { assertIsNotSelected() }
      agendaItem { assertIsNotSelected() }
      mapItem { assertIsNotSelected() }
      notificationItem { assertIsNotSelected() }

      dashboardScreen { assertIsDisplayed() }
      suggestionScreen { assertIsNotDisplayed() }
      agendaScreen { assertIsNotDisplayed() }
      mapScreen { assertIsNotDisplayed() }
      notificationScreen { assertIsNotDisplayed() }
    }
  }
}
