package com.github.se.wanderpals.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.DashboardViewModel
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.screens.trip.Dashboard
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val suggestion1: Suggestion =
    Suggestion(
        suggestionId = "1",
        userName = "User",
        createdAt = LocalDate.now().minusDays(1),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "1",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now(),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

private val suggestion2: Suggestion =
    Suggestion(
        suggestionId = "2",
        userName = "User",
        createdAt = LocalDate.now(),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "2",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now(),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

private val suggestion3: Suggestion =
    Suggestion(
        suggestionId = "3",
        userName = "User",
        createdAt = LocalDate.now(),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "3",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now(),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

private val suggestion4: Suggestion =
    Suggestion(
        suggestionId = "4",
        userName = "User",
        createdAt = LocalDate.now(),
        createdAtTime = LocalTime.now(),
        stop =
            Stop(
                stopId = "4",
                title = "Stop Title",
                address = "123 Street",
                date = LocalDate.now(),
                startTime = LocalTime.now(),
                duration = 60,
                budget = 100.0,
                description =
                    "This is a description of the stop. It should be brief and informative.",
                geoCords = GeoCords(0.0, 0.0),
                website = "https://example.com",
                imageUrl = ""),
        text = "This is a suggestion for a stop.",
        userId = "1")

class DashboardViewModelTest(list: List<Suggestion>) :
    DashboardViewModel(tripId = "", tripsRepository = TripsRepository("", Dispatchers.IO)) {
  private val _isLoading = MutableStateFlow(false)
  override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _state = MutableStateFlow(list)
  override val state: StateFlow<List<Suggestion>> = _state.asStateFlow()

  override fun loadSuggestion(tripId: String) {}

  fun setLoading(isLoading: Boolean) {
    _isLoading.value = isLoading
  }
}

@RunWith(AndroidJUnit4::class)
class DashboardTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockNavActions2: NavigationActions

  @Test
  fun testDashboardLoading() {
    val viewModel = DashboardViewModelTest(listOf())
    viewModel.setLoading(true)
    composeTestRule.setContent {
      Dashboard(
          tripId = "",
          dashboardViewModel = viewModel,
          oldNavActions = mockNavActions,
          navActions = mockNavActions2)
    }
    // Check that the loading indicator is displayed
    composeTestRule.onNodeWithTag("loading").assertIsDisplayed()

    viewModel.setLoading(false)
    composeTestRule.onNodeWithTag("loading").assertDoesNotExist()
  }

  @Test
  fun testDashboardDisplaysEssentials() {
    val viewModel = DashboardViewModelTest(listOf())
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(
          tripId = "",
          dashboardViewModel = viewModel,
          oldNavActions = mockNavActions,
          navActions = mockNavActions2)
    }

    // Check that the top bar is displayed
    composeTestRule.onNodeWithTag("dashboardTopBar", useUnmergedTree = true).assertIsDisplayed()
    // Check that the suggestion widget is displayed
    composeTestRule
        .onNodeWithTag("dashboardSuggestions", useUnmergedTree = true)
        .assertIsDisplayed()
    // Check that the suggestion widget title is displayed
    composeTestRule.onNodeWithTag("suggestionTitle", useUnmergedTree = true).assertIsDisplayed()
    // Check that the suggestion widget is displayed
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testDashboardOneSuggestions() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(
          tripId = "",
          dashboardViewModel = viewModel,
          oldNavActions = mockNavActions,
          navActions = mockNavActions2)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    // Check that the divider0 doesn't exist
    composeTestRule.onNodeWithTag("divider0", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun testDashboardTwoSuggestions() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1, suggestion2))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(
          tripId = "",
          dashboardViewModel = viewModel,
          oldNavActions = mockNavActions,
          navActions = mockNavActions2)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the second suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion2", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    // Check that the divider0 exist and is displayed
    composeTestRule
        .onNodeWithTag("divider0", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the divider1 doesn't exist
    composeTestRule.onNodeWithTag("divider1", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun testDashboardThreeSuggestions() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1, suggestion2, suggestion3))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(
          tripId = "",
          dashboardViewModel = viewModel,
          oldNavActions = mockNavActions,
          navActions = mockNavActions2)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the second suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion2", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the third suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    // Check that the divider0 exist and is displayed
    composeTestRule
        .onNodeWithTag("divider0", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the divider1 exist and is displayed
    composeTestRule
        .onNodeWithTag("divider1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the divider2 doesn't exist
    composeTestRule.onNodeWithTag("divider2", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun testDashboardFourSuggestions() {
    val viewModel =
        DashboardViewModelTest(listOf(suggestion1, suggestion2, suggestion3, suggestion4))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(
          tripId = "",
          dashboardViewModel = viewModel,
          oldNavActions = mockNavActions,
          navActions = mockNavActions2)
    }

    // Check that the suggestion widget doesn't display "No suggestions yet."
    composeTestRule.onNodeWithTag("noSuggestions", useUnmergedTree = true).assertDoesNotExist()

    // Check that the first suggestion doesn't exist
    composeTestRule.onNodeWithTag("suggestion1", useUnmergedTree = true).assertDoesNotExist()
    // Check that the second suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion2", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the third suggestion exist and is displayed
    composeTestRule
        .onNodeWithTag("suggestion3", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the fourth suggestion doesn't exist
    composeTestRule
        .onNodeWithTag("suggestion4", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    // Check that the divider0 exist and is displayed
    composeTestRule
        .onNodeWithTag("divider0", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the divider1 exist and is displayed
    composeTestRule
        .onNodeWithTag("divider1", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the divider2 doesn't exist
    composeTestRule.onNodeWithTag("divider2", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun testDashboardWidgetContent() {
    val viewModel = DashboardViewModelTest(listOf(suggestion1))
    viewModel.setLoading(false)
    composeTestRule.setContent {
      Dashboard(
          tripId = "",
          dashboardViewModel = viewModel,
          oldNavActions = mockNavActions,
          navActions = mockNavActions2)
    }

    // Check that the stop title is displayed
    composeTestRule
        .onNodeWithTag("stopTitle", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the created at date is displayed
    composeTestRule
        .onNodeWithTag("createdAt", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the user name is displayed
    composeTestRule
        .onNodeWithTag("userName", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    // Check that the time is displayed
    composeTestRule.onNodeWithTag("time", useUnmergedTree = true).assertExists().assertIsDisplayed()
  }
}
