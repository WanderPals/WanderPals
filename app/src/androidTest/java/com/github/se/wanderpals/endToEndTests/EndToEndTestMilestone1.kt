package com.github.se.wanderpals.endToEndTests

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.BuildConfig.MAPS_API_KEY
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.overview.OverviewViewModelTest
import com.github.se.wanderpals.screens.CreateTripoScreen
import com.github.se.wanderpals.screens.OverviewScreen
import com.github.se.wanderpals.screens.TripScreen
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.NavigationActionsVariables
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.navigation.globalVariables
import com.github.se.wanderpals.ui.navigation.rememberMultiNavigationAppState
import com.github.se.wanderpals.ui.screens.CreateTrip
import com.github.se.wanderpals.ui.screens.overview.Overview
import com.github.se.wanderpals.ui.screens.trip.Trip
import com.google.android.libraries.places.api.Places
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
class EndToEndTestMilestone1 : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockNavController: NavHostController

  @RelaxedMockK lateinit var mockNavController2: NavHostController

  @Before
  fun testSetup() {

    val context = ApplicationProvider.getApplicationContext<Context>()
    Places.initialize(context, MAPS_API_KEY)
    val placesClient = Places.createClient(context)

    composeTestRule.setContent {
      mockNavController = rememberNavController()
      mockNavController2 = rememberNavController()
      globalVariables = NavigationActionsVariables()
      mockNavActions =
          NavigationActions(
              mainNavigation =
                  rememberMultiNavigationAppState(
                      startDestination = Route.ROOT_ROUTE, mockNavController),
              tripNavigation =
                  rememberMultiNavigationAppState(
                      startDestination = Route.DASHBOARD, mockNavController2))
      val overviewViewModelTest = OverviewViewModelTest()

      NavHost(navController = mockNavController, startDestination = Route.OVERVIEW) {
        composable(Route.OVERVIEW) {
          BackHandler(true) {}
          Overview(overviewViewModel = overviewViewModelTest, navigationActions = mockNavActions)
        }
        composable(Route.CREATE_TRIP) {
          BackHandler(true) {}
          CreateTrip(overviewViewModelTest, mockNavActions)
        }
        composable(Route.TRIP) { navBackStackEntry ->
          BackHandler(true) {}
          Trip(
              mockNavActions,
              mockNavActions.variables.currentTrip,
              TripsRepository("-1", Dispatchers.IO),
              placesClient)
        }
      }
    }
  }

  @Test
  fun EndToEndTestMilestone1() {
    val overviewScreen = ComposeScreen.onComposeScreen<OverviewScreen>(composeTestRule) {}
    val createTripScreen = ComposeScreen.onComposeScreen<CreateTripoScreen>(composeTestRule) {}
    val tripScreen = ComposeScreen.onComposeScreen<TripScreen>(composeTestRule) {}

    overviewScreen {
      composeTestRule.onNodeWithText("Search a trip").performTextInput("new")
      noTripFoundOnSearchText { assertIsDisplayed() }
      composeTestRule.onNodeWithText("new").performTextClearance()
      createTripButton { performClick() }
    }
    createTripScreen {
      inputTitle {
        performTextClearance()
        performTextInput("new Trip")
      }
      inputBudget {
        performTextClearance()
        performTextInput("500")
      }
      inputDescription {
        performTextClearance()
        performTextInput("This is a new description")
      }
      inputStartDate {
        performTextClearance()
        performTextInput("01/01/2000")
      }
      inputEndDate {
        performTextClearance()
        performTextInput("01/01/3000")
      }
      saveButton { performClick() }

      overviewScreen {
        composeTestRule.onNodeWithText("Search a trip").performTextInput("new")
        noTripFoundOnSearchText { assertIsNotDisplayed() }
        composeTestRule.onNodeWithText("new").performTextClearance()
      }
      overviewScreen { buttonTrip2 { performClick() } }

      tripScreen { this.tripScreen { assertIsDisplayed() } }
      tripScreen {
        suggestionItem { performClick() }
        tripScreen.suggestionScreen { assertIsDisplayed() }
      }
    }
  }
}
