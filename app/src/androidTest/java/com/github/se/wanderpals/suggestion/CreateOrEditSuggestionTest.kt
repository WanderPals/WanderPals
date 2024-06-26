package com.github.se.wanderpals.suggestion

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.wanderpals.model.data.GeoCords
import com.github.se.wanderpals.model.data.Stop
import com.github.se.wanderpals.model.data.Suggestion
import com.github.se.wanderpals.model.repository.TripsRepository
import com.github.se.wanderpals.model.viewmodel.CreateSuggestionViewModel
import com.github.se.wanderpals.screens.CreateSuggestionScreen
import com.github.se.wanderpals.service.SessionManager
import com.github.se.wanderpals.ui.navigation.NavigationActions
import com.github.se.wanderpals.ui.navigation.Route
import com.github.se.wanderpals.ui.screens.suggestion.CreateOrEditSuggestion
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

val fixedTime = LocalTime.of(14, 30, 15)
private val testSuggestion: Suggestion =
    Suggestion(
        suggestionId = "",
        userId = "",
        userName = "tempUsername",
        text = "",
        createdAt = LocalDate.now(),
        createdAtTime = fixedTime,
        stop =
            Stop(
                stopId = "",
                title = "Stop",
                address = "",
                date = LocalDate.of(2024, 4, 16),
                startTime = LocalTime.of(12, 0),
                budget = 20.0,
                duration = 120,
                description = "This is a Stop",
                geoCords = GeoCords(0.0, 0.0),
                website = "www.example.com",
            ))

private val testSuggestion2: Suggestion =
    Suggestion(
        suggestionId = "",
        userId = "",
        userName = "tempUsername",
        text = "",
        createdAt = LocalDate.now(),
        createdAtTime = fixedTime,
        stop =
            Stop(
                stopId = "",
                title = "Stop",
                address = "",
                date = LocalDate.of(2024, 4, 16),
                startTime = LocalTime.of(12, 0),
                budget = 0.0,
                duration = 120,
                description = "This is a Stop",
                geoCords = GeoCords(0.0, 0.0),
            ))

open class CreateSuggestionViewModelTest(tripsRepository: TripsRepository) :
    CreateSuggestionViewModel(tripsRepository) {
  override fun addSuggestion(tripId: String, suggestion: Suggestion): Boolean {
    assert(
        suggestion == testSuggestion.copy(createdAtTime = suggestion.createdAtTime) ||
            suggestion ==
                testSuggestion2.copy(
                    createdAtTime =
                        suggestion.createdAtTime)) // We can't get the same LocalTime.Now
    return true
  }
}

@RunWith(AndroidJUnit4::class)
class CreateSuggestionTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Test
  fun createSuggestionReturnToSuggestionOnCancel() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      goBackButton {
        assertIsDisplayed()
        assertIsEnabled()
        performClick()
      }

      verify { mockNavActions.navigateTo(Route.DASHBOARD) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun createSuggestionReturnToSuggestionWhenSuccessful() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      SessionManager.setUserSession("testUser123", "tempUsername")

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
          performTextInput("Stop")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("20.0")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
          performTextInput("This is a Stop")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("12:00")
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("14:00")
        }

        inputAddress { assertIsNotDisplayed() }

        inputWebsite {
          assertIsDisplayed()
          performClick()

          assertTextContains("Website")

          performTextClearance()
          performTextInput("www.example.com")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions.navigateTo(Route.SUGGESTION) }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionWorksWithOnlyMandatoryField() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      SessionManager.setUserSession("testUser123", "tempUsername")

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
          performTextInput("Stop")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
          performTextInput("This is a Stop")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("12:00")
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("14:00")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions.navigateTo(Route.SUGGESTION) }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionFailsWithMissingTitle() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("20.0")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("12:00")
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("14:00")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
          performTextInput("This is a Stop")
        }

        inputWebsite {
          assertIsDisplayed()
          performClick()

          assertTextContains("Website")

          performTextClearance()
          performTextInput("www.example.com")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionFailsWithMissingStartDate() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
          performTextInput("Stop")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("20.0")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("12:00")
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("14:00")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
          performTextInput("This is a Stop")
        }

        inputWebsite {
          assertIsDisplayed()
          performClick()

          assertTextContains("Website")

          performTextClearance()
          performTextInput("www.example.com")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionFailsWithMissingEndDate() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
          performTextInput("Stop")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("20.0")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("12:00")
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("14:00")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
          performTextInput("This is a Stop")
        }

        inputWebsite {
          assertIsDisplayed()
          performClick()

          assertTextContains("Website")

          performTextClearance()
          performTextInput("www.example.com")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionFailsWithMissingStartTime() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
          performTextInput("Stop")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("20.0")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("14:00")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
          performTextInput("This is a Stop")
        }

        inputWebsite {
          assertIsDisplayed()
          performClick()

          assertTextContains("Website")

          performTextClearance()
          performTextInput("www.example.com")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionFailsWithMissingEndTime() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
          performTextInput("Stop")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("20.0")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("12:00")
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
          performTextInput("This is a Stop")
        }

        inputWebsite {
          assertIsDisplayed()
          performClick()

          assertTextContains("Website")

          performTextClearance()
          performTextInput("www.example.com")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionFailsWithMissingDescription() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create suggestion screen") {
        inputTitle {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Title*")

          performTextClearance()
          performTextInput("Stop")
        }

        inputBudget {
          assertIsDisplayed()
          performClick()

          assertTextContains("Budget")

          performTextClearance()
          performTextInput("20.0")
        }

        inputStartDate {
          assertIsDisplayed()

          assertTextContains("From*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputEndDate {
          assertIsDisplayed()

          assertTextContains("To*")

          performTextClearance()
          performTextInput("2024-04-16")
        }

        inputStartTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("12:00")
        }

        inputEndTime {
          assertIsDisplayed()

          assertTextContains("00:00")

          performTextClearance()
          performTextInput("14:00")
        }

        inputDescription {
          assertIsDisplayed()
          performClick()

          assertTextContains("Suggestion Description*")
          assertTextContains("Describe the suggestion")

          performTextClearance()
        }

        inputWebsite {
          assertIsDisplayed()
          performClick()

          assertTextContains("Website")

          performTextClearance()
          performTextInput("www.example.com")
        }

        createButton {
          assertIsDisplayed()
          performClick()
        }

        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun createSuggestionWithoutAddress() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion(
            "aaa",
            vm,
            onSuccess = { mockNavActions.navigateTo(Route.SUGGESTION) },
            onCancel = { mockNavActions.navigateTo(Route.DASHBOARD) })
      }

      step("Open create suggestion screen") { inputAddress { assertIsNotDisplayed() } }
    }
  }

  @Test
  fun createSuggestionWithAddress() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion("aaa", vm, Suggestion(stop = Stop(address = "Example address")))
      }

      inputAddress {
        assertIsDisplayed()

        assertTextContains("Example address")
      }
    }
  }

  @Test
  fun createSuggestionWithSuggestion() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent { CreateOrEditSuggestion("aaa", vm, suggestion = testSuggestion) }

      inputTitle {
        assertIsDisplayed()

        assertTextContains("Stop")
      }

      inputBudget {
        assertIsDisplayed()

        assertTextContains("20.0")
      }

      inputDescription {
        assertIsDisplayed()

        assertTextContains("This is a Stop")
      }

      inputStartDate {
        assertIsDisplayed()

        assertTextContains("2024-04-16")
      }

      inputEndDate {
        assertIsDisplayed()

        assertTextContains("2024-04-16")
      }

      inputStartTime {
        assertIsDisplayed()

        assertTextContains("12:00")
      }

      inputEndTime {
        assertIsDisplayed()

        assertTextContains("14:00")
      }

      inputAddress { assertIsNotDisplayed() }

      inputWebsite {
        assertIsDisplayed()

        assertTextContains("www.example.com")
      }
    }
  }

  @Test
  fun editSuggestion() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val vm = CreateSuggestionViewModelTest(TripsRepository("testUser123", Dispatchers.IO))
      composeTestRule.setContent {
        CreateOrEditSuggestion("aaa", vm, suggestion = Suggestion(suggestionId = "test"))
      }

      createButton {
        assertIsDisplayed()
        assertTextContains("Edit Suggestion")
      }
    }
  }

  /**
   * Test that the navigation to the CreateSuggestion screen is triggered when the button is
   * clicked.
   */
  @Test
  fun navigateToCreateSuggestionOnButtonPress() = run {
    ComposeScreen.onComposeScreen<CreateSuggestionScreen>(composeTestRule) {
      val suggestionVm = FakeSuggestionsViewModel()

      composeTestRule.setContent {
        com.github.se.wanderpals.ui.screens.trip.Suggestion(
            oldNavActions = mockNavActions,
            tripId = "testTripId",
            suggestionsViewModel = suggestionVm,
            onSuggestionClick = {
              mockNavActions.setVariablesTrip("testTripId")
              mockNavActions.navigateTo(Route.CREATE_SUGGESTION)
            })
      }

      // Simulate the button click that should lead to the CreateSuggestion screen
      suggestionButtonExists.performClick()

      // Verify that the navigation action to CreateSuggestion is called with the correct parameters
      verify {
        mockNavActions.setVariablesTrip("testTripId")
        mockNavActions.navigateTo(Route.CREATE_SUGGESTION)
      }
      confirmVerified(mockNavActions)
    }
  }

  /** Test if the CreateSuggestion screen is scrollable. */
  @Test
  fun checkIfCreateSuggestionIsScrollable() {
    val viewModel = CreateSuggestionViewModel(TripsRepository("testUser123", Dispatchers.IO))
    val suggestion =
        Suggestion(
            stop =
                Stop(
                    title = "Sample Title",
                    description = "Long description here to ensure scrollable content.",
                    geoCords = GeoCords(0.0, 0.0)))

    composeTestRule.setContent {
      CreateOrEditSuggestion("tripId", viewModel, suggestion = suggestion)
    }

    // Use swipeUp and swipeDown to simulate user scroll actions
    val scrollableNode = composeTestRule.onNodeWithText("Sample Title")

    // Perform a swipe up action to scroll down
    scrollableNode.performTouchInput { swipeUp() }

    // Perform a swipe down action to scroll back up
    scrollableNode.performTouchInput { swipeDown() }
  }
}
